package com.example.nvt.service;


import com.example.nvt.exceptions.NotFoundException;
import com.example.nvt.model.Client;
import com.example.nvt.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public Client findClientByValidValidation(String verificationCode){
        var clientWrapper = clientRepository.findClientByValidValidationCode(verificationCode);
        if(clientWrapper.isEmpty()) throw new NotFoundException("Client with this validation code does not exist");
        return clientWrapper.get();
    }
}
