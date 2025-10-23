package com.backend.avance1.service;

import com.backend.avance1.dto.ContactDTO;
import com.backend.avance1.dto.ContactReplyDTO;
import com.backend.avance1.dto.ContactDetailDTO;
import jakarta.mail.MessagingException;

public interface ContactServiceInterface {

    void sendContactMessage(ContactDTO dto) throws MessagingException;

    void replyToUser(ContactReplyDTO dto) throws MessagingException;

    ContactDetailDTO getContactByCode(String code);
}