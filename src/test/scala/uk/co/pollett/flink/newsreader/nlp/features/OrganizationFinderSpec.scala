package uk.co.pollett.flink.newsreader.nlp.features

import org.scalatest.FlatSpec

class OrganizationFinderSpec extends FlatSpec {

  val organizationFinder = new OrganizationFinder
  val listWithOrganization = List("was","not","accredited","by","the","US","Department","of","Education")

  "A list with an organization" should "return the name" in {
    assert(organizationFinder.parse(listWithOrganization) === List("US Department of Education"))
  }
}
