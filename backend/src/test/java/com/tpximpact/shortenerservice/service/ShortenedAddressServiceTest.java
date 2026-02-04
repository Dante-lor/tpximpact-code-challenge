package com.tpximpact.shortenerservice.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpximpact.shortenerservice.repository.ShortenedAddressDAO;

@ExtendWith(MockitoExtension.class)
public class ShortenedAddressServiceTest {

    @Mock
    private ShortenedAddressDAO dao;

    @InjectMocks
    private ShortenedAddressService service;

    
}
