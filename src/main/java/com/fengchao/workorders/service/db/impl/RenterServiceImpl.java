package com.fengchao.workorders.service.db.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fengchao.workorders.entity.Renter;
import com.fengchao.workorders.mapper.RenterMapper;
import com.fengchao.workorders.service.db.IRenterService;
import org.springframework.stereotype.Service;

@Service
public class RenterServiceImpl extends ServiceImpl<RenterMapper, Renter> implements IRenterService {
}
