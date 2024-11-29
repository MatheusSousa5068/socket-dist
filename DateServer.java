package com.gugawag.so.ipc;
/**
 * Time-of-day server listening to port 6013.
 *
 * Figure 3.21
 *
 * @author Silberschatz, Gagne, and Galvin. Pequenas alterações feitas por Gustavo Wagner (gugawag@gmail.com)
 * Operating System Concepts  - Ninth Edition
 * Copyright John Wiley & Sons - 2013.
 */

import java.net.*;
import java.io.*;
import java.util.Date;

public class DateServer {
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(6013);
			System.out.println("=== Servidor iniciado ===\n");

			while (true) {
				Socket clientSocket = serverSocket.accept();
				new ClientHandler(clientSocket).start();
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}
}

class ClientHandler extends Thread {
	private Socket clientSocket;

	public ClientHandler(Socket socket) {
		this.clientSocket = socket;
	}

	@Override
	public void run() {
		try {
			PrintWriter pout = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader bin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			pout.println(new Date().toString() + "-Boa noite alunos, sou Matheus Pereira de Sousa e fiz com minha dupla, Pablo Estrela!");

			String line = bin.readLine();
			System.out.println("O cliente me disse: " + line);


			Thread.sleep(30000);
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Erro ao comunicar com o cliente: " + e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
