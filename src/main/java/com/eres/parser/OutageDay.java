package com.eres.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OutageDay {

    private Set<Address> addresses;
    private LocalDate ldt;
    private Integer id;
    private String link;

    public OutageDay(Set<Address> addresses, LocalDate ldt, Integer id, String link) {
        this.addresses = addresses;
        this.ldt = ldt;
        this.id = id;
        this.link = link;
    }

    public OutageDay(String addresses, LocalDate ldt, Integer id, String link) {
        try{
            setAddressesFromJsonString(addresses);
            this.ldt = ldt;
            this.id = id;
            this.link = link;
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }
    }

    public String getAddressesToJsonString() throws JsonProcessingException {
       return new ObjectMapper().writeValueAsString(this.getAddresses());
    }

    public void setAddressesFromJsonString(String jsonString) throws JsonProcessingException {
        this.addresses = new ObjectMapper().readValue(jsonString, new TypeReference<Set<Address>>(){});
    }

    public Map<String, Map<String, Map<String, String>>> setOfAddressesToMap(){
        return this.getAddresses().stream()
                .collect(
                    Collectors.groupingBy(Address::getRegion,
                        Collectors.groupingBy(Address::getCity,
                            Collectors.toMap(Address::getStreet, Address::getDetails, (details1, details2) -> {
                                System.out.println("duplication found");
                                return details1;})
                        )));
    }

    public String getPrettyPrintString(String region, String city, String street){
        var map = this.setOfAddressesToMap();
        StringBuilder sb = new StringBuilder();
        map.keySet().stream()
                .filter(r -> r.equals(region) || region.isEmpty())
                .peek(r -> sb.append("\n<b>" + r + "</b>\n"))
                .forEach(r -> map.get(r).keySet().stream()
                        .filter((c -> c.equals(city) || city.isEmpty()))
                        .peek(c -> sb.append("<u>    " +c + ":" + "</u>\n"))
                        .forEach(c -> map.get(r).get(c).keySet().stream()
                            .filter(s -> s.equals(street) || street.isEmpty())
                            .forEach(s -> sb.append("        " + s)
                                            .append(map.get(r).get(c).get(s).isEmpty() ? "\n" : ", " + map.get(r).get(c).get(s) + "\n"))
                        )
                );
        return sb.toString();
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public LocalDate getLdt() {
        return ldt;
    }

    public void setLdt(LocalDate ldt) {
        this.ldt = ldt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
