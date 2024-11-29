package br.edu.ifpb.gugawag.so.sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Servidor2 {

    public static void main(String[] args) throws IOException {
        System.out.println("== Servidor ==");

        ServerSocket serverSocket = new ServerSocket(7001);


        while (true) {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            System.out.println("Cliente: " + socket.getInetAddress());

            String mensagem = dis.readUTF();
            System.out.println("Mensagem recebida: " + mensagem);
            String[] partes = mensagem.split(" ", 3);
            String comando = partes[0].toLowerCase();

            switch (comando) {
                case "readdir":
                    listarArquivos(dos);
                    break;
                case "rename":
                    if (partes.length == 3) {
                        renomearArquivo(dos, partes[1], partes[2]);
                    } else {
                        dos.writeUTF("Comando inválido. Use: rename <arquivo_antigo> <novo_nome>");
                    }
                    break;
                case "create":
                    if (partes.length == 2) {
                        criarArquivo(dos, partes[1]);
                    } else {
                        dos.writeUTF("Comando inválido. Use: create <nome_arquivo>");
                    }
                    break;
                case "remove":
                    if (partes.length == 2) {
                        removerArquivo(dos, partes[1]);
                    } else {
                        dos.writeUTF("Comando inválido. Use: remove <nome_arquivo>");
                    }
                    break;
                default:
                    dos.writeUTF("Comando desconhecido: " + comando);
                    break;
            }
        } catch (IOException e) {
            System.err.println("Erro ao comunicar com o cliente: " + e);
        }
    }

    private static void listarArquivos(DataOutputStream dos) throws IOException {
        File dir = new File("/home/ifpb/Documentos");
        if (dir.exists() && dir.isDirectory()) {
            String[] arquivos = dir.list();
            if (arquivos != null && arquivos.length > 0) {
                StringBuilder listaArquivos = new StringBuilder("Arquivos no diretório Documents:\n");
                for (String arquivo : arquivos) {
                    listaArquivos.append(arquivo).append("\n");
                }
                dos.writeUTF(listaArquivos.toString());
            } else {
                dos.writeUTF("Não há arquivos no diretório.");
            }
        } else {
            dos.writeUTF("O diretório 'Documents' não existe ou não é um diretório válido.");
        }
    }

    private static void renomearArquivo(DataOutputStream dos, String arquivoAntigo, String novoNome) throws IOException {
        File arquivo = new File("/home/ifpb/Documentos/" + arquivoAntigo);
        File novoArquivo = new File("/home/ifpb/Documentos/" + novoNome);

        if (arquivo.exists()) {
            if (arquivo.renameTo(novoArquivo)) {
                dos.writeUTF("Arquivo renomeado com sucesso de " + arquivoAntigo + " para " + novoNome);
            } else {
                dos.writeUTF("Erro ao renomear o arquivo.");
            }
        } else {
            dos.writeUTF("Arquivo não encontrado: " + arquivoAntigo);
        }
    }

    private static void criarArquivo(DataOutputStream dos, String nomeArquivo) throws IOException {
        File arquivo = new File("/home/ifpb/Documentos/" + nomeArquivo);

        if (arquivo.exists()) {
            dos.writeUTF("Arquivo já existe: " + nomeArquivo);
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
                writer.write("");
                dos.writeUTF("Arquivo criado com sucesso: " + nomeArquivo);
            } catch (IOException e) {
                dos.writeUTF("Erro ao criar o arquivo: " + nomeArquivo);
            }
        }
    }


    private static void removerArquivo(DataOutputStream dos, String nomeArquivo) throws IOException {
        File arquivo = new File("/home/ifpb/Documentos/" + nomeArquivo);

        if (arquivo.exists()) {
            if (arquivo.delete()) {
                dos.writeUTF("Arquivo removido com sucesso: " + nomeArquivo);
            } else {
                dos.writeUTF("Erro ao remover o arquivo.");
            }
        } else {
            dos.writeUTF("Arquivo não encontrado: " + nomeArquivo);
        }
    }
}
