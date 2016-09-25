package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Get;

import java.io.InputStream;


public class DriveServiceTest {

    DriveService driveService;

    @Mock
    TxcMakerParser txcMakerParser;
    @Mock
    GcsStreamFactory gcsStreamFactory;

    @Before
    public void setup() {
        driveService = new DriveService(txcMakerParser, gcsStreamFactory);

    }

    @Test
    public void testGetInputStreamForFile() throws Exception {
        Drive drive = mock(Drive.class);
        Files files = mock(Files.class);
        Get get = mock(Get.class);
        InputStream inputStream = mock(InputStream.class);

        when(drive.files()).thenReturn(files);
        when(files.get(any(String.class))).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);

        assertThat(driveService.getFileInputStream(drive, "someAudioId"), is(inputStream));
    }
}