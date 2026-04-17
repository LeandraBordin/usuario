package com.leandra.usuario.business;

import com.leandra.usuario.business.converter.UsuarioConverter;
import com.leandra.usuario.business.dto.UsuarioDTO;
import com.leandra.usuario.infrastructure.entity.Usuario;
import com.leandra.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class usuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
                return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }
}
