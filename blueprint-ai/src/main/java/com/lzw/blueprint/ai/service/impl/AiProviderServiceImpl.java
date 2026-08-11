package com.lzw.blueprint.ai.service.impl;

import com.lzw.blueprint.ai.entity.AiProvider;
import com.lzw.blueprint.ai.mapper.AiProviderMapper;
import com.lzw.blueprint.ai.provider.ProviderRouter;
import com.lzw.blueprint.ai.service.AiProviderService;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

/**
 * 简单 CRUD 实现，操作后刷新 ProviderRouter 缓存。
 */
@Service
public class AiProviderServiceImpl implements AiProviderService {

    private final AiProviderMapper aiProviderMapper;
    private final ProviderRouter providerRouter;

    public AiProviderServiceImpl(AiProviderMapper aiProviderMapper, ProviderRouter providerRouter) {
        this.aiProviderMapper = aiProviderMapper;
        this.providerRouter = providerRouter;
    }

    @Override
    public int insert(AiProvider entity) {
        int rows = aiProviderMapper.insert(entity);
        providerRouter.clearCache();
        return rows;
    }

    @Override
    public int deleteById(Long id) {
        int rows = aiProviderMapper.deleteById(id);
        providerRouter.clearCache();
        return rows;
    }

    @Override
    public int updateById(AiProvider entity) {
        int rows = aiProviderMapper.updateById(entity);
        providerRouter.clearCache();
        return rows;
    }

    @Override
    public AiProvider selectById(Long id) {
        return aiProviderMapper.selectById(id);
    }

    @Override
    public List<AiProvider> selectList() {
        // 返回所有供应商（包括已禁用），按 priority 降序
        return aiProviderMapper.selectList(new QueryWrapper<AiProvider>().orderByDesc("priority"));
    }

    @Override
    public PageResult<AiProvider> selectPage(PageQuery query) {
        // 暂未实现分页逻辑，返回空结果占位
        return new PageResult<>();
    }
}
