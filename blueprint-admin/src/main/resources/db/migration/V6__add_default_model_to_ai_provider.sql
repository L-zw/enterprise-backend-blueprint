-- ============================================================
-- V6__add_default_model_to_ai_provider.sql
-- 给供应商表添加默认模型字段
-- ============================================================

ALTER TABLE ai_provider
    ADD COLUMN default_model VARCHAR(100) DEFAULT 'gpt-3.5-turbo';

COMMENT ON COLUMN ai_provider.default_model IS '默认调用的模型名称';