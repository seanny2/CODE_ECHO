package com.projectx.codeecho.service;

import com.projectx.codeecho.domain.dto.RoomDto;

public interface RoomService {

    boolean isPresent(String roomId);

    boolean createRoom(RoomDto room);
    boolean deleteRoom(RoomDto room);
}
