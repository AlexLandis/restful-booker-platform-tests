package com.automationintesting.unit.service;

import com.automationintesting.db.MessageDB;
import com.automationintesting.model.db.Count;
import com.automationintesting.model.db.Message;
import com.automationintesting.model.db.MessageSummary;
import com.automationintesting.model.db.Messages;
import com.automationintesting.model.service.MessageResult;
import com.automationintesting.requests.AuthRequests;
import com.automationintesting.service.MessageService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class MessageServiceTest {

    @Mock
    private MessageDB messageDB;

    @Mock
    private AuthRequests authRequests;

    @Autowired
    @InjectMocks
    private MessageService messageService;

    @Before
    public void initialiseMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMessagesTest() throws SQLException {
        List<MessageSummary> sampleMessages = new ArrayList<MessageSummary>(){{
            this.add(new MessageSummary(1, "Mark", "Message 1"));
            this.add(new MessageSummary(1, "Richard", "Message 2"));
        }};

        when(messageDB.queryMessages()).thenReturn(sampleMessages);

        Messages messages = messageService.getMessages();

        assertThat(messages.toString(), is("Messages{messages=[MessageSummary{id=1, name='Mark', subject='Message 1'}, MessageSummary{id=1, name='Richard', subject='Message 2'}]}"));
    }

    @Test
    public void getCountTest() throws SQLException {
        when(messageDB.getUnreadCount()).thenReturn(10);

        Count count = messageService.getCount();

        assertThat(count.toString(), is("Count{count=10}"));
    }

    @Test
    public void getMessageTest() throws SQLException {
        Message sampleMessage = new Message("Mark", "test@email.com", "0189271231", "Test Subject", "Test Description");

        when(messageDB.query(1)).thenReturn(sampleMessage);

        MessageResult messageResult = messageService.getSpecificMessage(1);

        assertThat(messageResult.getHttpStatus(), is(HttpStatus.OK));
        assertThat(messageResult.getMessage().toString(), is("Message{messageid=0, name='Mark', email='test@email.com', phone='0189271231', subject='Test Subject', description='Test Description'}"));
    }

    @Test
    public void getMessageNotFoundTest() throws SQLException {
        when(messageDB.query(0)).thenReturn(null);

        MessageResult message = messageService.getSpecificMessage(0);

        assertThat(message.getHttpStatus(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void createMessageTest() throws SQLException {
        Message sampleMessage = new Message("Mark", "test@email.com", "0189271231", "Test Subject", "Test Description");

        when(messageDB.create(sampleMessage)).thenReturn(sampleMessage);

        Message message = messageService.createMessage(sampleMessage);

        assertThat(message.toString(), is("Message{messageid=0, name='Mark', email='test@email.com', phone='0189271231', subject='Test Subject', description='Test Description'}"));
    }

    @Test
    public void deleteMessageTest() throws SQLException {
        when(authRequests.postCheckAuth("abc")).thenReturn(true);
        when(messageDB.delete(1)).thenReturn(true);

        MessageResult messageResult = messageService.deleteMessage(1, "abc");

        assertThat(messageResult.getHttpStatus(), is(HttpStatus.ACCEPTED));
    }

    @Test
    public void deleteMessageNotFoundTest() throws SQLException {
        when(authRequests.postCheckAuth("abc")).thenReturn(true);
        when(messageDB.delete(1)).thenReturn(false);

        MessageResult messageResult = messageService.deleteMessage(1, "abc");

        assertThat(messageResult.getHttpStatus(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteMessageNotAuthenticatedTest() throws SQLException {
        when(authRequests.postCheckAuth("abc")).thenReturn(false);

        MessageResult messageResult = messageService.deleteMessage(1, "abc");

        assertThat(messageResult.getHttpStatus(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void markMessageAsReadTest() throws SQLException {
        when(authRequests.postCheckAuth("abc")).thenReturn(true);
        doNothing().when(messageDB).markAsRead(1);

        HttpStatus messageStatus = messageService.markAsRead(1, "abc");

        assertThat(messageStatus, is(HttpStatus.ACCEPTED));
    }

    @Test
    public void markMessageAsReadNotAuthenticated() throws SQLException {
        when(authRequests.postCheckAuth("abc")).thenReturn(false);

        HttpStatus messageStatus = messageService.markAsRead(1, "abc");

        assertThat(messageStatus, is(HttpStatus.FORBIDDEN));
    }

}
