package com.automationintesting.unit.model;

import com.automationintesting.model.db.Booking;
import com.automationintesting.model.db.Message;
import com.automationintesting.service.MessageBuilder;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MessageBuilderTest {

    @Test
    public void messageBuiltFromBookingTest(){
        Date checkindate = new GregorianCalendar(1990,1,1).getTime();
        Date checkoutdate = new GregorianCalendar(1990,1,2).getTime();

        Booking booking = new Booking.BookingBuilder()
                .setFirstname("Mark")
                .setLastname("Winteringham")
                .setDepositpaid(true)
                .setCheckin(checkindate)
                .setCheckout(checkoutdate)
                .setEmail("mark@mwtestconsultancy.co.uk")
                .setPhone("01392123928")
                .build();

        MessageBuilder messageBuilder = new MessageBuilder();
        Message message = messageBuilder.build(booking);

        assertThat(message.toString(), is("Message{, name='Mark Winteringham', email='mark@mwtestconsultancy.co.uk', phone='01392123928', subject='You have a new booking!', description='You have a new booking from Mark Winteringham. They have booked a room for the following dates: 1990-02-01 to 1990-02-02'}"));
    }

}
