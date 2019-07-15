package com.mobiquityinc.packer.io;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.domain.PackTask;
import com.mobiquityinc.packer.domain.Thing;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PackerTaskReaderTest {

    @Test
    public void whenCorrectFormatThenReadTasks() throws APIException {
        PackTaskReader reader = new PackTaskReader(new ByteArrayInputStream(
            ("" +
                "81 : (1,53.38,€45) (2,88.62,€98)\n" +
                "8 : (1,15.3,€34)"
            ).getBytes()
        ));
        List<PackTask> tasks = reader.readAll();
        assertThat(tasks).hasSize(2);
        assertThat(tasks.get(0).getMaxWeight()).isEqualTo(81);
        assertThat(tasks.get(0).getThings()).hasSize(2);
        assertThat(tasks.get(0).getThings()).containsOnly(
            new Thing(1, 53.38, 45.0),
            new Thing(2, 88.62, 98.0)
        );
        assertThat(tasks.get(1).getMaxWeight()).isEqualTo(8);
        assertThat(tasks.get(1).getThings()).hasSize(1);
        assertThat(tasks.get(1).getThings()).containsOnly(
            new Thing(1, 15.3, 34.0)
        );
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(reader::readNext);
    }

    @Test
    public void whenFormatIsNotCorrectThenExceptionThrown() throws APIException {
        assertThatExceptionOfType(APIException.class).isThrownBy(() -> {
            new PackTaskReader(new ByteArrayInputStream(
                ("" +
                    "81 ; (1,53.38,€45) (2,88.62,€98)\n" +
                    "8 : (1,15.3,€34)"
                ).getBytes()
            )).readAll();
        });
        assertThatExceptionOfType(APIException.class).isThrownBy(() -> {
            new PackTaskReader(new ByteArrayInputStream(
                ("" +
                    "81 ; (1,53.38,€45) (2,88.62,€98)d"
                ).getBytes()
            )).readAll();
        });
    }

}
