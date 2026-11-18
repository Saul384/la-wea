package com.microserviciob.microb.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.microserviciob.microb.dto.EntityADto;
import java.util.List;

@FeignClient(name = "msa") 
public interface ClientFeignMSA {
    @PostMapping("/api/entity-a/by-ids")
    List<EntityADto> obtenerDTOsDelMSA(@RequestBody List<Integer> ids);
}