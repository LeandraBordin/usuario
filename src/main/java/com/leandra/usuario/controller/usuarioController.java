package com.leandra.usuario.controller;

import com.leandra.usuario.business.dto.UsuarioDTO;
import com.leandra.usuario.business.usuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class usuarioController {

    private usuarioService usuarioService;
    @PostMapping
    public ResponseEntity<UsuarioDTO> salvaUsuario(@RequestBody UsuarioDTO usuarioDTO){
    return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }

}
