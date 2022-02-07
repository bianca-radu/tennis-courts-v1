package com.tenniscourts.reservations;

import com.tenniscourts.tenniscourts.TennisCourt;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class CreateReservationRequestDTO {

  @NotNull
  private Long guestId;

  @NotNull
  private Long scheduleId;

  @NotNull
  private List<TennisCourt> tennisCourtList;

}
