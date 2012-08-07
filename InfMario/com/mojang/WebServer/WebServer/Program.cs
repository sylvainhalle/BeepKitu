using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.Net;
using System.Net.Sockets;

namespace WebServer
{
    class Program
    {
        static void Main(string[] args)
        {
            Int32 port = 80;
            IPAddress localAddress = IPAddress.Parse("0.0.0.0");
            Byte[] bytes = new Byte[256];
            String data = null;

            while (true)
            {
                Console.Write("Waiting for a connection ... ");

                TcpClient client = new TcpClient();
                client.Connect(localAddress, port);

                Console.WriteLine("Connected");

                data = null;

                NetworkStream stream = client.GetStream();

                int i;
                while ((i = stream.Read(bytes, 0, bytes.Length)) != 0)
                {
                    data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                    Console.WriteLine("Received: {0}", data);
                    data = data.ToUpper();

                    byte[] msg = System.Text.Encoding.ASCII.GetBytes(data);
                    stream.Write(msg, 0, msg.Length);
                    Console.WriteLine("Sent: {0}", data);
                }

                client.Close();
            }
        }
    }
}
