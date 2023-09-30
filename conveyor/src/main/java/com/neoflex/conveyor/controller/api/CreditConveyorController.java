package com.neoflex.conveyor.controller.api;

import com.neoflex.conveyor.dto.request.LoanApplicationServiceDTO;
import com.neoflex.conveyor.dto.request.ScoringDataServiceDTO;
import com.neoflex.conveyor.dto.response.CreditDTO;
import com.neoflex.conveyor.dto.request.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.response.CreditServiceDTO;
import com.neoflex.conveyor.dto.response.LoanOfferDTO;
import com.neoflex.conveyor.dto.request.ScoringDataDTO;
import com.neoflex.conveyor.dto.mapper.CreditMapper;
import com.neoflex.conveyor.dto.mapper.LoanApplicationMapper;
import com.neoflex.conveyor.dto.mapper.LoanOfferMapper;
import com.neoflex.conveyor.dto.mapper.ScoringDataMapper;
import com.neoflex.conveyor.dto.response.LoanOfferServiceDTO;
import com.neoflex.conveyor.service.CreditService;
import com.neoflex.conveyor.service.LoanOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/conveyor")
@Slf4j
@Tag(name="Контроллер кредитного конвейера",
     description="Принимает заявки от пользователей для первичного и полного расчета кредита")
public class CreditConveyorController {

    private final LoanOfferService loanOfferService;
    private final CreditService creditService;
    private final CreditMapper creditMapper;
    private final ScoringDataMapper scoringDataMapper;
    private final LoanApplicationMapper loanApplicationMapper;
    private final LoanOfferMapper loanOfferMapper;

    @Operation(
            summary = "Прием предварительной заявки на кредит",
            description = "Позволяет рассчитать предварительную стоимость кредита на основе ограниченного количества " +
                    "данных - необходимы паспортные данные - и отдает пользователю четыре предложения, " +
                    "сортируя от худшего (с самой большой ставкой) к лучшему (с самой маленькой ставкой)."
    )
    @PostMapping("/offers")
    public List<LoanOfferDTO> preCalculateLoan(@Valid  @RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("Got request for creating loan offers for {}", requestDTO);

        LoanApplicationServiceDTO applicationServiceDTO = loanApplicationMapper.toApplication(requestDTO);
        List<LoanOfferServiceDTO> loanOfferServiceDTOs = loanOfferService.preCalculateLoan(applicationServiceDTO);

        return loanOfferServiceDTOs.stream()
                               .map(loanOfferMapper::toDto)
                               .toList();
    }
    @Operation(
            summary = "Прием полной заявки на кредит для точного расчета суммы займа",
            description = "Помогает расчитать итоговую ставку, полную стомость кредита, подготовить график платежей " +
                    "на основе полной информации о заемщике: необходимы паспортные данные, информация о семье, доходах" +
                    " и работе."
    )
    @PostMapping("/calculation")
    public CreditDTO calculateLoan(@Valid @RequestBody ScoringDataDTO requestDto) {
        log.info("Got request for creating a new credit offer: full calculating for {}", requestDto);

        ScoringDataServiceDTO dataServiceDTO = scoringDataMapper.toData(requestDto);
        CreditServiceDTO creditServiceDTO = creditService.calculateLoan(dataServiceDTO);

        return creditMapper.toDto(creditServiceDTO);
    }
}
