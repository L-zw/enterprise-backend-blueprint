package com.lzw.blueprint.ai.mapper;

import com.lzw.blueprint.ai.entity.AiProvider;
import com.lzw.blueprint.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AiProviderMapper extends BaseMapper<AiProvider> {

    @Select("SELECT * FROM ai_provider WHERE enabled = TRUE ORDER BY priority DESC")
    List<AiProvider> findEnabled();
}