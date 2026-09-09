package com.lzw.blueprint.ai.service.prompt;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PromptRendererTest {

    private final PromptRenderer renderer = new PromptRenderer();

    @Test
    void nullTemplateReturnsNull() {
        assertThat(renderer.render(null, Map.of("a", "1"))).isNull();
    }

    @Test
    void noVariablesReturnsAsIs() {
        assertThat(renderer.render("hello world", null)).isEqualTo("hello world");
    }

    @Test
    void replacesVariable() {
        String result = renderer.render("请解释 ${code}", Map.of("code", "int x;"));
        assertThat(result).isEqualTo("请解释 int x;");
    }

    @Test
    void missingVariableValueBecomesEmpty() {
        java.util.HashMap<String, String> vars = new java.util.HashMap<>();
        vars.put("a", null);
        String result = renderer.render("a=${a}", vars);
        assertThat(result).isEqualTo("a=");
    }

    @Test
    void unknownVariableStays() {
        String result = renderer.render("a=${missing}", Map.of("a", "1"));
        assertThat(result).isEqualTo("a=${missing}");
    }
}
