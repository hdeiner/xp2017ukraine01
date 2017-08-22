package com.deinersoft.timeteller;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Properties;

public class TimeTeller {

    public static void main(String [] args) {

        System.out.println(getResult(1,1,false));
        System.out.println(getResult(2,1,false));
        System.out.println(getResult(1,2,false));
        System.out.println(getResult(2,2,false));
        System.out.println(getResult(1,1,true));
        System.out.println(getResult(2,1,true));
        System.out.println(getResult(1,2,true));
        System.out.println(getResult(2,2,true));

    }

    static String getResult(int whichOne, int how, boolean special) {
        String result = "";
        int i = 0;
        int j = 0;
        int k = 0;

        switch (whichOne) {
            case 1:
                i = LocalDateTime.now().getHour();
                j = LocalDateTime.now().getMinute();
                k = LocalDateTime.now().getSecond();
                break;
            case 2:
                LocalDateTime t = LocalDateTime.now(Clock.systemUTC());
                i = t.getHour();
                j = t.getMinute();
                k = t.getSecond();
                break;
        }

        switch (how) {
            case 1:
                result = String.format("%02d:%02d:%02d", i, j, k);
                if (whichOne == 2) {
                    result += "Z";
                }
                break;
            case 2:
                String[] words = {"twelve", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven"};
                String[] fuzzWords = {"", "almost ten after", "ten after", "a quarter after", "twenty after", "almost half past", "half past", "almost twenty before", "twenty before", "a quarter of", "ten of", "almost"};
                String[] partOfDay = {"at night", "in the morning", "in the afternoon", "in the evening"};

                if (k >= 30) j++;

                if (j >= 3) {
                    result += fuzzWords[(j+2)/5] + " ";
                }
                if (j < 35) {
                    result += words[i%12];
                }  else {
                    result += words[(i+1)%12];
                }

                result += " " + partOfDay[i/6];

                if (whichOne == 2) {
                    result += " Zulu";
                }

                break;
        }

        if (special) {
            Properties properties = System.getProperties();

            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("howarddeiner.xyzzy@gmail.com", "birneraccount");
                        }
                    });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("howarddeiner.xyzzy@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("howard.deiner@deinersoft.com"));
                message.setSubject("TimeTeller");
                message.setText("The time is now " + result);

                Transport.send(message);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }
}
