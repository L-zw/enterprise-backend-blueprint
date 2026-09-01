package com.lzw.blueprint.ai.service.audit;

import com.lzw.blueprint.common.ResultCode;
import com.lzw.blueprint.common.exception.BusinessException;

public class UsageLimitExceededException extends BusinessException {

    public UsageLimitExceededException(long limit) {
        super(ResultCode.TOO_MANY_REQUESTS.getCode(), "Daily usage limit exceeded: " + limit);
    }
}
