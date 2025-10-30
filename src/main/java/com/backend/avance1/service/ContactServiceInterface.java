package com.backend.avance1.service;

import com.backend.avance1.dto.ContactDTO;
import com.backend.avance1.dto.ContactReplyDTO;
import com.backend.avance1.dto.ContactDetailDTO;
import com.backend.avance1.entity.ContactMessage;
import jakarta.mail.MessagingException;
import java.util.List;

public interface ContactServiceInterface {

    void sendContactMessage(ContactDTO dto) throws MessagingException;

    void replyToUser(ContactReplyDTO dto) throws MessagingException;

    ContactDetailDTO getContactByCode(String code);

    List<ContactMessage> getAllContactMessages();
}