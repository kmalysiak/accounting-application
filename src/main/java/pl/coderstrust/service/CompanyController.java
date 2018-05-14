package pl.coderstrust.service;


import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Company;
import pl.coderstrust.service.filters.CompanyDummyFilter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("v2/company")
@RestController
public class CompanyController extends AbstractController<Company> {

  public CompanyController(CompanyService companyService, CompanyDummyFilter dummyFilter) {
    super.service = companyService;
    super.filter = dummyFilter;
  }

  @RequestMapping(value = "", method = RequestMethod.POST)
  @ApiOperation(value = "Adds the company and returning id")
  public synchronized ResponseEntity addCompany(@RequestBody Company company) {
    return super.addEntry(company, null);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ApiOperation(value = "Returns the company by id")
  public synchronized ResponseEntity getCompanyById(@PathVariable("id") Long companyId) {
    return super.getEntryById(companyId, null);
  }

  @RequestMapping(value = "/term", method = RequestMethod.GET)
  public List<Company> getCompanyByTerm(@RequestParam("term") String term) {
    List<Company> selectedCompanies = service.getEntry().stream()
        .filter(company -> company.getName().contains(term)).collect(Collectors.toList());
    return selectedCompanies;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  @ApiOperation(value = "Returns the list of companies in the specified date range")
  public synchronized ResponseEntity getCompanyByDate(
      @RequestParam(name = "startDate", required = false) LocalDate startDate,
      @RequestParam(name = "endDate", required = false) LocalDate endDate) {
    return super.getEntryByDate(startDate, endDate, null);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @ApiOperation(value = "Updates the company by id")
  public synchronized ResponseEntity updateCompany(@PathVariable("id") Long id,
      @RequestBody Company company) {
    return super.updateEntry(id, company, null);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ApiOperation(value = "Deletes the company by id")
  public synchronized ResponseEntity removeCompany(@PathVariable("id") Long id) {
    return removeEntry(id, null);
  }
}