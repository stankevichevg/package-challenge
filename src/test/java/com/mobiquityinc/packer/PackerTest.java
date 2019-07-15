package com.mobiquityinc.packer;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.exception.APIException.ValidationException;
import com.mobiquityinc.packer.io.PackTaskReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


public class PackerTest {

    @Test
    public void successPathTest() throws APIException {
        assertThat(
            Packer.pack(getClass().getClassLoader().getResource("success_test_case.txt").getPath())
        ).isEqualTo(
            "" +
                "4\n" +
                "-\n" +
                "2,7\n" +
                "8,9\n"
        );
    }

    @Test
    public void whenIncorrectFilePathThenApiExceptionThrown() {
        assertThatExceptionOfType(APIException.class).isThrownBy(() -> Packer.pack("broken_path"));
    }

    @Test
    public void whenTaskHasManyThingsThanExceptionThrown() {
        Assertions.assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> {
            Packer.pack(new ByteArrayInputStream(
                ("" +
                    "81 : (1,53.38,€45) (2,88.62,€98) (3,88.62,€98) (4,53.38,€45) (5,88.62,€98) " +
                    "(6,88.62,€98) (7,53.38,€45) (8,88.62,€98) (9,88.62,€98) (10,53.38,€45) (11,88.62,€98) " +
                    "(12,88.62,€98) (13,53.38,€45) (14,88.62,€98) (15,88.62,€98) (16,88.62,€98)"
                ).getBytes()
            ));
        }).withMessage("Task might have up to %s things to pack from, given: %s", 15, 16);
    }

    @Test
    public void whenTaskHasBigMaxWeightThanExceptionThrown() {
        Assertions.assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> {
            Packer.pack(new ByteArrayInputStream(
                ("" +
                    "200 : (1,53.38,€45)"
                ).getBytes()
            ));
        }).withMessage("Max weight that a package can take is ≤ %s, given: %s", 100.0, 200.0);
    }

    @Test
    public void whenThingHasBigWeightThanExceptionThrown() {
        Assertions.assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> {
            Packer.pack(new ByteArrayInputStream(
                ("" +
                    "20 : (1,153.38,€45)"
                ).getBytes()
            ));
        }).withMessage("Max weight that a thing can have is ≤ %s, given: %s", 100.0, 153.38);
    }

    @Test
    public void whenThingHasBigCostThanExceptionThrown() {
        Assertions.assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> {
            Packer.pack(new ByteArrayInputStream(
                ("" +
                    "20 : (1,53.38,€145)"
                ).getBytes()
            ));
        }).withMessage("Max cost that a thing can have is ≤ %s, given: %s", 100.0, 145.0);
    }

}
