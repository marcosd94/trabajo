package py.com.excelsis.sicca.util;

   import java.lang.annotation.Documented;
   import java.lang.annotation.ElementType;
  import java.lang.annotation.Retention;
   import java.lang.annotation.RetentionPolicy;
   import java.lang.annotation.Target;
   
import org.hibernate.validator.ValidatorClass;
import org.jboss.seam.captcha.CaptchaResponseValidator;
   
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   @Target(ElementType.METHOD)
   @ValidatorClass(CaptchaResponseValidator.class)
   public @interface CaptchaResponse  
   {
      String message() default "#{messages['msg_captcha']}";
   }