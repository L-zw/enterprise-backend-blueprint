package com.lzw.blueprint.ai.service.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SseEmitterManagerTest {

    private SseEmitterManager manager;

    @BeforeEach
    void setUp() {
        manager = new SseEmitterManager();
    }

    @Test
    void registerStoresEmitter() {
        SseEmitter emitter = manager.register("s1");
        assertThat(manager.get("s1")).isSameAs(emitter);
    }

    @Test
    void getUnknownReturnsNull() {
        assertThat(manager.get("none")).isNull();
    }

    @Test
    void removeClearsEmitter() {
        manager.register("s1");
        manager.remove("s1");
        assertThat(manager.get("s1")).isNull();
    }

    @Test
    void removeUnknownIsNoOp() {
        assertThatCode(() -> manager.remove("none")).doesNotThrowAnyException();
    }
}
