package com.example.campusCircle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Channel.ChannelCategory;
import com.example.campusCircle.repository.ChannelRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelService Unit Tests")
class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    private Channel testChannel;

    @BeforeEach
    void setUp() {
        testChannel = Channel.builder()
                .id(1L)
                .name("Test Channel")
                .description("A test channel")
                .universityId(1L)
                .category(ChannelCategory.ACADEMICS)
                .subscriberCount(100)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createChannel should save channel when name does not exist")
    void createChannel_WhenNameNotExists_ShouldSave() {
        // Arrange
        Channel newChannel = Channel.builder()
                .name("New Channel")
                .universityId(1L)
                .build();

        when(channelRepository.existsByUniversityIdAndName(1L, "New Channel")).thenReturn(false);
        when(channelRepository.save(any(Channel.class))).thenAnswer(invocation -> {
            Channel saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        Channel result = channelService.createChannel(newChannel);

        // Assert
        assertNotNull(result.getCreatedAt());
        assertTrue(result.getIsActive());
        assertEquals(0, result.getSubscriberCount());
        verify(channelRepository).save(newChannel);
    }

    @Test
    @DisplayName("createChannel should throw exception when name already exists")
    void createChannel_WhenNameExists_ShouldThrowException() {
        // Arrange
        Channel newChannel = Channel.builder()
                .name("Test Channel")
                .universityId(1L)
                .build();

        when(channelRepository.existsByUniversityIdAndName(1L, "Test Channel")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> channelService.createChannel(newChannel));
        assertEquals("Channel with this name already exists in this university", exception.getMessage());
        verify(channelRepository, never()).save(any());
    }

    @Test
    @DisplayName("getChannel should return channel when exists")
    void getChannel_WhenExists_ShouldReturnChannel() {
        // Arrange
        when(channelRepository.findById(1L)).thenReturn(Optional.of(testChannel));

        // Act
        Channel result = channelService.getChannel(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testChannel.getId(), result.getId());
        assertEquals(testChannel.getName(), result.getName());
    }

    @Test
    @DisplayName("getChannel should throw exception when not found")
    void getChannel_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(channelRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> channelService.getChannel(99L));
        assertEquals("Channel not found", exception.getMessage());
    }

    @Test
    @DisplayName("getChannelByName should return channel")
    void getChannelByName_ShouldReturnChannel() {
        // Arrange
        when(channelRepository.findByName("Test Channel")).thenReturn(Optional.of(testChannel));

        // Act
        Channel result = channelService.getChannelByName("Test Channel");

        // Assert
        assertNotNull(result);
        assertEquals("Test Channel", result.getName());
    }

    @Test
    @DisplayName("getAllChannels should return list of active channels")
    void getAllChannels_ShouldReturnActiveChannelsList() {
        // Arrange
        List<Channel> channels = Arrays.asList(testChannel);
        when(channelRepository.findActiveChannelsOrderBySubscribers()).thenReturn(channels);

        // Act
        List<Channel> result = channelService.getAllChannels();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("updateChannel should only update provided fields")
    void updateChannel_ShouldOnlyUpdateProvidedFields() {
        // Arrange
        Channel updatedChannel = Channel.builder()
                .name("Updated Name")
                .description("Updated description")
                .build();
        when(channelRepository.findById(1L)).thenReturn(Optional.of(testChannel));
        when(channelRepository.save(any(Channel.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Channel result = channelService.updateChannel(1L, updatedChannel);

        // Assert
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertNotNull(result.getUpdatedAt());
        assertEquals(1L, result.getUniversityId()); // Unchanged
    }

    @Test
    @DisplayName("deleteChannel should set isActive to false")
    void deleteChannel_ShouldSetIsActiveFalse() {
        // Arrange
        when(channelRepository.findById(1L)).thenReturn(Optional.of(testChannel));
        when(channelRepository.save(any(Channel.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        channelService.deleteChannel(1L);

        // Assert
        ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
        verify(channelRepository).save(channelCaptor.capture());
        assertFalse(channelCaptor.getValue().getIsActive());
        assertNotNull(channelCaptor.getValue().getUpdatedAt());
    }

    @Test
    @DisplayName("incrementSubscriberCount should increment by one")
    void incrementSubscriberCount_ShouldIncrementByOne() {
        // Arrange
        testChannel.setSubscriberCount(100);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(testChannel));
        when(channelRepository.save(any(Channel.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        channelService.incrementSubscriberCount(1L);

        // Assert
        ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
        verify(channelRepository).save(channelCaptor.capture());
        assertEquals(101, channelCaptor.getValue().getSubscriberCount());
    }

    @Test
    @DisplayName("decrementSubscriberCount should decrement by one")
    void decrementSubscriberCount_ShouldDecrementByOne() {
        // Arrange
        testChannel.setSubscriberCount(100);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(testChannel));
        when(channelRepository.save(any(Channel.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        channelService.decrementSubscriberCount(1L);

        // Assert
        ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
        verify(channelRepository).save(channelCaptor.capture());
        assertEquals(99, channelCaptor.getValue().getSubscriberCount());
    }

    @Test
    @DisplayName("decrementSubscriberCount should not go below zero")
    void decrementSubscriberCount_ShouldNotGoBelowZero() {
        // Arrange
        testChannel.setSubscriberCount(0);
        when(channelRepository.findById(1L)).thenReturn(Optional.of(testChannel));

        // Act
        channelService.decrementSubscriberCount(1L);

        // Assert
        verify(channelRepository, never()).save(any()); // Should not save when count is 0
    }

    @Test
    @DisplayName("getChannelsByUniversity should return channels for university")
    void getChannelsByUniversity_ShouldReturnChannelsForUniversity() {
        // Arrange
        List<Channel> channels = Arrays.asList(testChannel);
        when(channelRepository.findActiveByUniversity(1L)).thenReturn(channels);

        // Act
        List<Channel> result = channelService.getChannelsByUniversity(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getUniversityId());
    }

    @Test
    @DisplayName("getChannelsByCategory should return channels for category")
    void getChannelsByCategory_ShouldReturnChannelsForCategory() {
        // Arrange
        List<Channel> channels = Arrays.asList(testChannel);
        when(channelRepository.findByCategory(ChannelCategory.ACADEMICS)).thenReturn(channels);

        // Act
        List<Channel> result = channelService.getChannelsByCategory(ChannelCategory.ACADEMICS);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(ChannelCategory.ACADEMICS, result.get(0).getCategory());
    }

    @Test
    @DisplayName("searchChannels should return matching channels")
    void searchChannels_ShouldReturnMatchingChannels() {
        // Arrange
        List<Channel> channels = Arrays.asList(testChannel);
        when(channelRepository.searchChannels("Test")).thenReturn(channels);

        // Act
        List<Channel> result = channelService.searchChannels("Test");

        // Assert
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getName().contains("Test"));
    }
}
