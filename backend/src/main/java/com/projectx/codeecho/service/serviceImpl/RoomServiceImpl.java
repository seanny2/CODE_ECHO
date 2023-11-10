package com.projectx.codeecho.service.serviceImpl;

import com.projectx.codeecho.domain.dto.RoomDto;
import com.projectx.codeecho.domain.entity.RoomEntity;
import com.projectx.codeecho.repository.RoomRepository;
import com.projectx.codeecho.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public boolean isPresent(String roomId) {
        boolean result = false;

        try {
            RoomEntity foundRoom = roomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException(this.getClass().toString()));

            result = true;

        } catch (IllegalArgumentException e) {
            System.out.printf("[%s]: '%s'는 존재하지 않는 회의실입니다.\n", e.getMessage(), roomId);
        }

        return result;
    }

    @Override
    public boolean createRoom(RoomDto room) {
        System.out.printf("[%s]: %s", this.getClass().toString(), "회의실 생성 메소드 실행\n");
        try {
            if (!isPresent(room.getId())) {
                roomRepository.save(
                        RoomEntity.builder()
                                .id(room.getId())
                                .build()
                );
                System.out.printf("[%s]: '%s'을 생성하였습니다.\n", this.getClass().toString(), room.getId());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.printf("[%s]: '%s'을 생성할 수 없습니다.\n", this.getClass().toString(), room.getId());
            return false;
        }
    }

    @Override
    public boolean deleteRoom(RoomDto room) {
        System.out.printf("[%s]: %s", this.getClass().toString(), "회의실 삭제 메소드 실행\n");
        try {
            if (isPresent(room.getId())) {
                roomRepository.deleteById(room.getId());
                System.out.printf("[%s]: '%s'을 삭제하였습니다.\n", this.getClass().toString(), room.getId());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.printf("[%s]: '%s'을 삭제할 수 없습니다.\n", this.getClass().toString(), room.getId());
            return false;
        }
    }
}
