"""Exercise real RCON framing, including fragmented reads and authentication errors."""
import pathlib
import socket
import struct
import sys
import threading
import unittest

sys.path.insert(0, str(pathlib.Path(__file__).parents[1]))
from gameplay_client import Rcon


def read_packet(sock):
    def read(count):
        data = b''
        while len(data) < count:
            chunk = sock.recv(count - len(data))
            if not chunk:
                raise ConnectionError('truncated packet')
            data += chunk
        return data
    size, = struct.unpack('<i', read(4))
    body = read(size)
    request, kind = struct.unpack('<ii', body[:8])
    return request, kind, body[8:-2].decode()


def packet(request, kind, text=''):
    body = struct.pack('<ii', request, kind) + text.encode() + b'\0\0'
    return struct.pack('<i', len(body)) + body


class RconTest(unittest.TestCase):
    def server(self, action):
        listener = socket.socket()
        listener.bind(('127.0.0.1', 0))
        listener.listen(1)
        port = listener.getsockname()[1]
        errors = []
        def serve():
            try:
                with listener:
                    client, _ = listener.accept()
                    with client:
                        client.settimeout(5)
                        action(client)
            except Exception as error:
                errors.append(error)
        thread = threading.Thread(target=serve, daemon=True)
        thread.start()
        self.addCleanup(thread.join, 2)
        self.addCleanup(listener.close)
        return port, errors, thread

    def test_fragmented_authentication_and_unicode_response(self):
        def serve(sock):
            request, kind, text = read_packet(sock)
            self.assertEqual((kind, text), (3, 'test-password'))
            # Vanilla can emit a response packet before the authentication result.
            for byte in packet(request, 0) + packet(request, 2):
                sock.sendall(bytes([byte]))
            request, kind, text = read_packet(sock)
            self.assertEqual((kind, text), (2, 'data get entity PinChatTest Pos'))
            for byte in packet(request, 0, 'Координаты: [1.5d, 64.0d, -2.5d]'):
                sock.sendall(bytes([byte]))
        port, errors, thread = self.server(serve)
        rcon = Rcon(port, 'test-password')
        self.addCleanup(rcon.close)
        self.assertEqual(rcon.command('data get entity PinChatTest Pos'), 'Координаты: [1.5d, 64.0d, -2.5d]')
        thread.join(2)
        self.assertEqual(errors, [])

    def test_rejected_password_is_a_failure(self):
        def serve(sock):
            read_packet(sock)
            sock.sendall(packet(-1, 2))
        port, _, _ = self.server(serve)
        with self.assertRaisesRegex(RuntimeError, 'authentication failed'):
            Rcon(port, 'wrong-password')

    def test_disconnected_socket_is_not_treated_as_an_empty_response(self):
        def serve(sock):
            request, _, _ = read_packet(sock)
            sock.sendall(packet(request, 2))
            read_packet(sock)
            sock.sendall(struct.pack('<i', 50) + b'truncated')
        port, _, _ = self.server(serve)
        rcon = Rcon(port, 'test-password')
        self.addCleanup(rcon.close)
        with self.assertRaisesRegex(ConnectionError, 'closed'):
            rcon.command('list')


if __name__ == '__main__':
    unittest.main()
