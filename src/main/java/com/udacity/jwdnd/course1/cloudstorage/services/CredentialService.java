package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {

    private CredentialMapper credentialMapper;
    private EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    private Credential encryptPassword(Credential credential)
    {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), encodedKey);
        credential.setKey(encodedKey);
        credential.setPassword(encryptedPassword);
        return credential;
    }

    private Credential decryptPassword(Credential credential)
    {
        String decryptedPassword = encryptionService.decryptValue(credential.getPassword(), credential.getKey());
        credential.setPassword(decryptedPassword);
        return credential;
    }
    public int insertCredential(Credential credential)
    {
       return  credentialMapper.InsertCredential(encryptPassword(credential));
    }

    public int updateCredential(Credential credential)
    {
        return  credentialMapper.updateCredential(encryptPassword(credential));
    }

    public List<Credential> getAllCredentials(Integer userId)
    {
        List<Credential> credentials = credentialMapper.getCredentialsOfUser(userId);

        // Decrypt passwords for each credential
        for (Credential credential : credentials) {
            String decryptedPassword = encryptionService.decryptValue(credential.getPassword(), credential.getKey());
            credential.setDecryptPassword(decryptedPassword);
        }
        return credentials;
    }

    public int deleteCredential(Integer credentialId)
    {
        return credentialMapper.deleteCredential(credentialId);
    }
}
