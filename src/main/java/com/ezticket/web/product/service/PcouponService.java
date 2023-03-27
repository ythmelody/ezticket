package com.ezticket.web.product.service;

import com.ezticket.web.product.dto.PcouponDTO;
import com.ezticket.web.product.dto.PcouponStatusDTO;
import com.ezticket.web.product.pojo.Pcoupon;
import com.ezticket.web.product.repository.PcouponRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PcouponService {
    @Autowired
    private PcouponRepository pcouponRepository;

    @Autowired
    private ModelMapper modelMapper;


    public PcouponDTO getPcouponByID(Integer id) {
        Pcoupon pcoupon = pcouponRepository.getReferenceById(id);
        return EntityToDTO(pcoupon);
    }
    public List<PcouponDTO> getPcouponsByID(Integer id) {
        return pcouponRepository.findById(id)
                .stream()
                .map(this::EntityToDTO)
                .collect(Collectors.toList());
    }
    public PcouponDTO EntityToDTO(Pcoupon pcoupon){
        return modelMapper.map(pcoupon, PcouponDTO.class);
    }
    public List<PcouponDTO> getAllPcoupon(){
        return pcouponRepository.findAll()
                .stream()
                .map(this::EntityToDTO)
                .collect(Collectors.toList());
    }
    public PcouponStatusDTO updateByID(Integer id, byte processStatus) {
        Pcoupon pcoupon = pcouponRepository.getReferenceById(id);
        pcoupon.setPcouponstatus(processStatus);
        Pcoupon updatedPcoupon = pcouponRepository.save(pcoupon);
        return EntityToStatusDTO(updatedPcoupon);
    }
    public PcouponStatusDTO EntityToStatusDTO(Pcoupon pcoupon){
        return modelMapper.map(pcoupon, PcouponStatusDTO.class);
    }
}
