package com.tenniscourts.guests;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GuestService {

  private final GuestRepository guestRepository;

  public Guest createGuest(Guest guest) {
    return guestRepository.save(guest);
  }

  public void updateGuest(Guest guest) {
    guestRepository.save(guest);
  }

  public void deleteGuest(Guest guest) {
    guestRepository.delete(guest);
  }

  public Optional<Guest> findById(Long id) {
    return guestRepository.findById(id);
  }

  public List<Guest> findByName(String name) {
    return guestRepository.findByName(name);
  }

  public List<Guest> listAllGuests() {
    return guestRepository.findAll();
  }
}
