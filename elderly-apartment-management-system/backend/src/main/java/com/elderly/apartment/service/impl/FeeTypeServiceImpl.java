package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.FeeType;
import com.elderly.apartment.mapper.FeeTypeMapper;
import com.elderly.apartment.service.FeeTypeService;
import org.springframework.stereotype.Service;

@Service
public class FeeTypeServiceImpl extends ServiceImpl<FeeTypeMapper, FeeType> implements FeeTypeService {
}
