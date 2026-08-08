package com.lzw.blueprint.ai.mapper;

import com.lzw.blueprint.ai.entity.AiUsageRecord;
import com.lzw.blueprint.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AiUsageRecordMapper extends BaseMapper<AiUsageRecord> {

    @Select("SELECT COALESCE(SUM(tokens_input + tokens_output), 0) FROM ai_usage_record WHERE user_id = #{userId} AND record_date = CURRENT_DATE")
    long todayTokensByUser(@Param("userId") Long userId);

    @Insert("INSERT INTO ai_usage_record (user_id, provider_code, model_id, tokens_input, tokens_output, request_count, record_date, create_time) " +
            "VALUES (#{userId}, #{providerCode}, #{modelId}, #{tokensInput}, #{tokensOutput}, #{requestCount}, CURRENT_DATE, NOW())")
    void insertDaily(AiUsageRecord record);
}