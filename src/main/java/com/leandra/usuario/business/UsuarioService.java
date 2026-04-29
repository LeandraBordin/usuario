package com.leandra.usuario.business;

import com.leandra.usuario.business.converter.UsuarioConverter;
import com.leandra.usuario.business.dto.EnderecoDTO;
import com.leandra.usuario.business.dto.TelefoneDTO;
import com.leandra.usuario.business.dto.UsuarioDTO;
import com.leandra.usuario.infrastructure.entity.Endereco;
import com.leandra.usuario.infrastructure.entity.Telefone;
import com.leandra.usuario.infrastructure.entity.Usuario;
import com.leandra.usuario.infrastructure.exceptions.ConflictException;
import com.leandra.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.leandra.usuario.infrastructure.repository.EnderecoRepository;
import com.leandra.usuario.infrastructure.repository.TelefoneRepository;
import com.leandra.usuario.infrastructure.repository.UsuarioRepository;
import com.leandra.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

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

    public UsuarioDTO buscaUsuarioPorEmail(String email){
        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                    ()-> new ResourceNotFoundException("email não encontrado"+email)));
        } catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Email nao encontrado"+email);
        }

    }
    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO usuarioDTO){
        // busca email do usuario pelo token
        String email = jwtUtil.extractUsername(token.substring(7));
        // encripta senha de usuario
        usuarioDTO.setSenha(usuarioDTO.getSenha() != null ? passwordEncoder.encode(usuarioDTO.getSenha()) : null );
        //busca usuario no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email nao localizado"));

        //mesclou dados do usuario recebido com o usuario do BD
        Usuario usuario = usuarioConverter.updateUsuario(usuarioDTO,usuarioEntity);

        //salva dados do usuario convertido e converte para UsuarioDTO
        return  usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario)) ;
    }
    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){
        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
            new ResourceNotFoundException("Id nao encontrado"+idEndereco));
        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO,entity);
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }
    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO telefoneDTO){
        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado"+idTelefone));
        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO,entity);
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }
}
