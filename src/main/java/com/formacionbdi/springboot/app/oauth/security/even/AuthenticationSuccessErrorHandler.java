package com.formacionbdi.springboot.app.oauth.security.even;

import com.formacionbdi.springboot.app.oauth.services.IUsuarioService;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

    private Logger logger = LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);


    @Autowired
    IUsuarioService iUsuarioService;


    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        System.out.println("Success Login :" + user.getUsername());
        logger.info("Succes Login: " + user.getUsername());
        Usuario usuario = iUsuarioService.findByUsername(authentication.getName());
        if (usuario.getIntentos() != null && usuario.getIntentos() > 0 ){
            usuario.setIntentos(0);
        }

        iUsuarioService.update(usuario,usuario.getId());


    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException e, Authentication authentication) {
        logger.info("Error en el login :" + e.getMessage());
        try {
            Usuario usuario = iUsuarioService.findByUsername(authentication.getName());
            if(usuario.getIntentos()==null){
                usuario.setIntentos(0);
            }


            usuario.setIntentos(usuario.getIntentos()+1);
            logger.info("Intentos fallidos =" + usuario.getIntentos());
            if(usuario.getIntentos() >= 3){
                logger.error(String.format("Usuario %s desabilitado por max intentos",usuario.getNombre()));
                usuario.setEnabled(false);
            }

            iUsuarioService.update(usuario,usuario.getId());
        } catch (FeignException ex) {
            logger.error(String.format("EL usuario %s no existe en el sistema ", authentication.getName()));
        }

    }
}
