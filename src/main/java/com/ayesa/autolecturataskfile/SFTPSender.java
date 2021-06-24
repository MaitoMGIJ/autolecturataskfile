/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ayesa.autolecturataskfile;

import java.io.IOException;
import java.io.File;

import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.SSHClient;

/**
 *
 * @author felipe
 */
public class SFTPSender {
    
    private final String localFilePath;
    private final String fileName;
    private final String SFTPServer;
    private final String SFTPUser;
    private final String SFTPKeyFilePath;
    private final String SFTPFilePath;
    
    
    public SFTPSender(ControlParameters cp){
        this.localFilePath = cp.getLocalFilePath();
        this.fileName = cp.getFinalFileName();
        this.SFTPServer = cp.getSFTPServer();
        this.SFTPUser = cp.getSFTPUser();
        this.SFTPKeyFilePath = cp.getSFTPKeyFilePath();
        this.SFTPFilePath = cp.getSFTPFilePath();
    }
    

    private SSHClient setupSshj() throws IOException {
        final SSHClient client = new SSHClient();
        client.addHostKeyVerifier((HostKeyVerifier)new PromiscuousVerifier());
        client.connect(SFTPServer);
        final File privateKey = new File(SFTPKeyFilePath);
        final KeyProvider keys = client.loadKeys(privateKey.getPath());
        client.authPublickey(SFTPUser, new KeyProvider[] { keys });
        return client;
    }
    
    
    public void onUploadFile() {
        try {
            System.out.println("### Enviar csv...");
            final SSHClient sshClient = this.setupSshj();
            final SFTPClient sftpClient = sshClient.newSFTPClient();
            sftpClient.put(localFilePath + fileName, SFTPFilePath + fileName);
            sftpClient.close();
            sshClient.disconnect();
            System.out.println("### Finaliza envío csv satisfactoriamente...");
        } catch (IOException ex) {
            System.out.println("### Error al enviar csv: " + ex.getMessage());
        }
    }
    
}
