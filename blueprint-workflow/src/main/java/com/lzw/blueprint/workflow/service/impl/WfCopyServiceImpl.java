package com.lzw.blueprint.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.entity.WfCopy;
import com.lzw.blueprint.workflow.mapper.WfCopyMapper;
import com.lzw.blueprint.workflow.service.WfCopyService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WfCopyServiceImpl implements WfCopyService {

    private final WfCopyMapper copyMapper;

    public WfCopyServiceImpl(WfCopyMapper copyMapper) {
        this.copyMapper = copyMapper;
    }

    @Override
    public int copy(String instanceId, List<Long> userIds) {
        if (instanceId == null || instanceId.isBlank()) {
            throw new BusinessException(400, "流程实例不能为空");
        }
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException(400, "抄送人列表不能为空");
        }
        int rows = 0;
        for (Long userId : userIds) {
            WfCopy copy = new WfCopy();
            copy.setInstanceId(instanceId);
            copy.setUserId(userId);
            copy.setReadStatus("UNREAD");
            copyMapper.insert(copy);
            rows++;
        }
        return rows;
    }

    @Override
    public List<WfCopy> listByUser(Long userId) {
        return copyMapper.selectList(new QueryWrapper<WfCopy>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
    }

    @Override
    public int markRead(Long id) {
        WfCopy copy = copyMapper.selectById(id);
        if (copy == null) {
            throw new BusinessException(404, "抄送记录不存在: " + id);
        }
        copy.setReadStatus("READ");
        copy.setReadTime(LocalDateTime.now());
        return copyMapper.updateById(copy);
    }
}
