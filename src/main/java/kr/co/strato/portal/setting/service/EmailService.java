package kr.co.strato.portal.setting.service;

import java.util.Properties;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {
	
	@Value("${smtp.host}")
	private String smtpHost;
	
	@Value("${smtp.port}")
	private int smtpPort;
	
	@Value("${smtp.user.id}")
	private String smtpUser;
	
	@Value("${smtp.user.password}")
	private String smtpPassword;
	
	/**
	 * 메일 전송
	 * @param toEmail
	 * @return
	 */
	public void sendMail(String toEmail, String title, String contents) {
		SendEmailRunnable runnable = new SendEmailRunnable(toEmail, title, contents);
		Executors.newSingleThreadExecutor().submit(runnable);
	}
	
	class SendEmailRunnable implements Runnable {
		private String toEmail;
		private String title;
		private String contents;
		
		public SendEmailRunnable(String toEmail, String title, String contents) {
			this.toEmail = toEmail;
			this.title = title;
			this.contents = contents;
		}

		@Override
		public void run() {			
			// SMTP 서버 정보를 설정.
			Properties prop = new Properties();
			prop.put("mail.smtp.host", smtpHost);
			prop.put("mail.smtp.port", smtpPort);
			prop.put("mail.smtp.auth", "true");
			prop.put("mail.smtp.starttls.enable", "true");
			
			SMTPAuthenticator authenticator = new SMTPAuthenticator(smtpUser, smtpPassword);
			Session session = Session.getDefaultInstance(prop, authenticator);

			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(smtpUser));

				// 수신자 메일주소
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

				// 메일 제목
				message.setSubject(title);

				// 메일 본문
				message.setContent(contents, "text/html; charset=euc-kr");

				// 메일 전송
				Transport.send(message);
				
				log.info("Send Email: {}", toEmail);
				log.info("Send Email - title: {}", title);
				log.info("Send Email - contents {}", contents);
			} catch (AddressException e) {
				log.error(e.getMessage());
			} catch (MessagingException e) {
				log.error(e.getMessage());
			}
		}
	}
	
	class SMTPAuthenticator extends Authenticator {
		protected String username;
		protected String password;

		public SMTPAuthenticator(String user, String pwd) {
			username = user;
			password = pwd;
		}
		
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}
}
