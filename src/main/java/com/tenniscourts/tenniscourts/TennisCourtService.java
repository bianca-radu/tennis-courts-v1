package com.tenniscourts.tenniscourts;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.ScheduleService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TennisCourtService {

  private final TennisCourtRepository tennisCourtRepository;

  private final ScheduleService scheduleService;

  private final TennisCourtMapper tennisCourtMapper;

  private final GuestService guestService;

  public TennisCourtDTO addTennisCourt(TennisCourtDTO tennisCourt) {
    return tennisCourtMapper
        .map(tennisCourtRepository.saveAndFlush(tennisCourtMapper.map(tennisCourt)));
  }

  public TennisCourtDTO findTennisCourtById(Long id) {
    return tennisCourtRepository.findById(id).map(tennisCourtMapper::map)
        .<EntityNotFoundException>orElseThrow(() -> {
          throw new EntityNotFoundException("Tennis Court not found.");
        });
  }

  public TennisCourtDTO findTennisCourtWithSchedulesById(Long tennisCourtId) {
    TennisCourtDTO tennisCourtDTO = findTennisCourtById(tennisCourtId);
    tennisCourtDTO
        .setTennisCourtSchedules(scheduleService.findSchedulesByTennisCourtId(tennisCourtId));
    return tennisCourtDTO;
  }

  public Guest createGuest(Guest guest) {
    return guestService.createGuest(guest);
  }

  public void updateGuest(Guest guest) {
    guestService.updateGuest(guest);
  }

  public void deleteGuest(Guest guest) {
    guestService.deleteGuest(guest);
  }

  public Optional<Guest> findById(Long id) {
    return guestService.findById(id);
  }

  public List<Guest> findByName(String name) {
    return guestService.findByName(name);
  }

  public List<Guest> listAllGuests() {
    return guestService.listAllGuests();
  }
}
