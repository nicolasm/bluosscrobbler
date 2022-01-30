package com.nicolasm.bluosscrobbler.bluos.poller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ScheduledTask.class,
        BluOSStatusPoller.class
})
class ScheduledTaskTest {
    @Autowired
    private ScheduledTask task;

    @MockBean
    private BluOSStatusPoller poller;

    @Test
    void pollStatus() {
        assertThat(task.isEnabled()).isTrue();
        task.pollStatus();
        assertThat(task.isEnabled()).isTrue();

        verify(poller).poll();
    }
}