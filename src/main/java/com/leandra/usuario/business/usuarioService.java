package com.leandra.usuario.business;

import com.leandra.usuario.business.converter.UsuarioConverter;
import com.leandra.usuario.business.dto.UsuarioDTO;
import com.leandra.usuario.infrastructure.entity.Usuario;
import com.leandra.usuario.infrastructure.exceptions.ConflictException;
import com.leandra.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.leandra.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class usuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        try{
            emailExiste(usuarioDTO.getEmail());
            usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
            Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
        } catch (ConflictException error) {
            throw new ConflictException("Email já cadastrado"+ error.getCause());
        }
    }
    public void emailExiste(String email){
        try{
            boolean exist = verificaEmailExistente(email);
            if (exist){
                throw new ConflictException("Email já cadastrado"+email);
            }
        }catch (ConflictException error){
            throw new ConflictException("Email já cadastrado"+ error.getCause());
        }
    }
    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscaUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("email não encontrado"+email));
    }
}
