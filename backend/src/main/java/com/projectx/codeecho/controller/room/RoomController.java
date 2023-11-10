package com.projectx.codeecho.controller.room;

import com.projectx.codeecho.domain.dto.RoomDto;
import com.projectx.codeecho.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 회의실 관련 컨트롤러.
 */
@Controller
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    /**
     * 회의실 뷰 불러오기 (DB에 존재하는 회의실이 없으면 홈으로)
     *
     * @param roomId 회의실 아이디
     * @return the model and view
     */
    @GetMapping(value="/{roomId}")
    public String roomView(@PathVariable String roomId, Model model) {
        if (!roomService.isPresent(roomId))
            return "redirect:/";

        model.addAttribute("roomId", roomId);
        return "room";
    }


    /**
     * 회의실 생성하기 (DB에 존재하지 않으면 생성)
     *
     * @param room 클라이언트에서 생성한 roomId를 여기로 가져와서 DB에 저장.
     * @return 성공하면 200 OK, 실패하면 404 Error.
     */
    @PostMapping("/create")
    public ResponseEntity<String> roomCreate(@RequestBody RoomDto room) {
        if (roomService.createRoom(room)) {
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>("회의실을 생성할 수 없습니다.", HttpStatus.NOT_FOUND);
    }

    /**
     * 회의실 삭제하기 (DB에 존재하지 않으면 생성)
     *
     * @param room 클라이언트에서 생성한 roomId를 여기로 가져와서 DB에 저장.
     * @return 성공하면 200 OK, 실패하면 404 Error.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> roomDelete(@RequestBody RoomDto room) {
        if (roomService.deleteRoom(room)) {
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>("회의실을 삭제할 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}