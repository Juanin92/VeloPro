package cl.jic.VeloPro.Utility;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Entity.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EmailService {

    @Autowired private JavaMailSender mailSender;

    public void sendEmailDebtDelay(Costumer costumer, TicketHistory ticket){
        String to = costumer.getEmail();
        String subject = "Aviso: Su ticket ha vencido";
        String text = "Hola " + costumer.getName() + ",\nTe recordamos que tu boleta " + ticket.getDocument()
                + " ha vencido. Con fecha de compra " + ticket.getDate() + ".\nPor favor, póngase en contacto con nosotros para el pago.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendEmailWithReceipt(Costumer costumer, Sale sale, String filePath) {
        String to = costumer.getEmail();
        String subject = "Confirmación de Venta - " + sale.getDocument();
        String text = "Hola " + costumer.getName() + ",\nSe ha generado una venta " + sale.getDocument()
                + " con el método de préstamo con un valor de $" + sale.getTotalSale() + ".\nAgradecemos su compra. \nAdjunto encontrarás la boleta de tu compra.";
        if ("x@x.xxx".equals(to)){
            return;
        } else {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom("barbara.juanito.go@gmail.com");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text);

                FileSystemResource file = new FileSystemResource(filePath);
                if (file.exists()) {
                    helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
                } else {
                    throw new IllegalArgumentException("El archivo PDF no existe: " + filePath);
                }

                mailSender.send(message);
            } catch (Exception e) {
                throw new IllegalArgumentException("No fue posible enviar el correo");
            }
        }
    }

    public void sendCodePass(User user, String code){
        try{
            String to = user.getEmail();
            String subject = "Cambio Contraseña";
            String text = "Hola " + user.getName() + ",\n Te enviamos tu código para un cambio de contraseña: " + code
                    + ".\nPor favor, ingresa este código en el campo de contraseña actual y crea una nueva en el campo de contraseña nueva";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        }catch (Exception e){
            throw new IllegalArgumentException("No fue posible enviar el correo");
        }
    }
}
