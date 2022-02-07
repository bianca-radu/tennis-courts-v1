package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;

  private final ReservationMapper reservationMapper;

  public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
    Reservation reservation = reservationMapper.map(createReservationRequestDTO);
    int tennisCourtsNr = createReservationRequestDTO.getTennisCourtList().size();
    reservation.setValue(new BigDecimal(10 * tennisCourtsNr));
    reservationRepository.save(reservation);
    return reservationMapper.map(reservation);
  }

  public ReservationDTO findReservation(Long reservationId) {
    return reservationRepository.findById(reservationId).map(reservationMapper::map)
        .<EntityNotFoundException>
            orElseThrow(() -> {
          throw new EntityNotFoundException("Reservation not found.");
        });
  }

  public ReservationDTO cancelReservation(Long reservationId) {
    return reservationMapper.map(this.cancel(reservationId));
  }

  private Reservation cancel(Long reservationId) {
    return reservationRepository.findById(reservationId).map(reservation -> {

      this.validateCancellation(reservation);

      BigDecimal refundValue = getRefundValue(reservation);
      return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

    }).<EntityNotFoundException>orElseThrow(() -> {
      throw new EntityNotFoundException("Reservation not found.");
    });
  }

  private Reservation updateReservation(Reservation reservation, BigDecimal refundValue,
      ReservationStatus status) {
    reservation.setReservationStatus(status);
    reservation.setValue(reservation.getValue().subtract(refundValue));
    reservation.setRefundValue(refundValue);

    return reservationRepository.save(reservation);
  }

  private void validateCancellation(Reservation reservation) {
    if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
      throw new IllegalArgumentException(
          "Cannot cancel/reschedule because it's not in ready to play status.");
    }

    if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
    }
  }

  public BigDecimal getRefundValue(Reservation reservation) {
    long hours = ChronoUnit.HOURS
        .between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

    if (hours >= 24) {
      return reservation.getValue();
    } else if (hours > 12) {
      return BigDecimal.valueOf(75).multiply(reservation.getValue())
          .divide(BigDecimal.valueOf(100), RoundingMode.UP);
    } else if (hours > 2) {
      return BigDecimal.valueOf(50).multiply(reservation.getValue())
          .divide(BigDecimal.valueOf(100), RoundingMode.UP);
    } else if (hours >= 0.1) {
      return BigDecimal.valueOf(25).multiply(reservation.getValue())
          .divide(BigDecimal.valueOf(100), RoundingMode.UP);
    }

    return BigDecimal.ZERO;
  }

  /*TODO: This method actually not fully working, find a way to fix the issue when it's throwing the error:
          "Cannot reschedule to the same slot.*/
  public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {
    Reservation previousReservation = cancel(previousReservationId);
    previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
    reservationRepository.save(previousReservation);

    ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
        .guestId(previousReservation.getGuest().getId())
        .scheduleId(scheduleId)
        .build());
    newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
    return newReservation;
  }
}
