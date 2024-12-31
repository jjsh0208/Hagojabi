package com.ddong_kka.hagojabi.Users.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private static int number;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public static void createNumber(){
        number = (int) (Math.random() * (9000)) + 10000;
    }

    public MimeMessage createMail(String email){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            message.setFrom("jjsh0208@gmail.com");
            message.setRecipients(MimeMessage.RecipientType.TO,email);
            message.setSubject("[Hagojabi] 이메일 인증 번호 발송");

            String body = "<html>" +
                    "<body style='background-color: #000000; margin: 0 auto; max-width: 600px; word-break: break-word; padding: 50px 20px; color: #ffffff; font-family: Arial, sans-serif;'>" +
                    "  <div style='text-align: center;'>" +
                    "    <h1 style='font-size: 28px; font-weight: bold; margin-bottom: 30px;'>이메일 주소 인증</h1>" +
                    "    <p style='font-size: 16px; opacity: 0.8; line-height: 1.6; font-weight: 400; margin-bottom: 40px;'>" +
                    "      안녕하세요? <strong>Hagojabi </strong> 관리자입니다.<br>" +
                    "      Hagojabi 서비스를 사용하시려면 회원가입 시 입력하신 이메일 주소의 인증이 필요합니다.<br>" +
                    "      아래 인증 번호를 입력하셔서 이메일 인증을 완료해주세요.<br>" +
                    "      감사합니다." +
                    "    </p>" +
                    "    <div style='margin: 20px auto; padding: 15px 0; width: 100%; max-width: 300px; color: #000000; font-size: 24px; font-weight: bold; text-align: center; background-color: #f4f4f4; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);'>" +
                    "      " + number +
                    "    </div>" +
                    "    <p style='margin-top: 30px; font-size: 14px; opacity: 0.6;'>" +
                    "      이 이메일은 자동 발송된 메시지입니다. 답변하지 마세요." +
                    "    </p>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            message.setText(body,"UTF-8","html");
        }catch (MessagingException e){
            e.printStackTrace();
        }

        return message;
    }

    public void sendMail(String mail){

        MimeMessage message = createMail(mail);
        javaMailSender.send(message);
    }

    public boolean verifyCode(String code) {
        // 입력받은 코드와 저장된 인증 코드 비교
        return code.equals(String.valueOf(number));
    }

}
