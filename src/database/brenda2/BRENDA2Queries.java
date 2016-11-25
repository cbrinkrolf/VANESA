package database.brenda2;

public class BRENDA2Queries {
	/*
	// getting reaction from ec / organism
	use dawismd;
	SELECT * FROM dawismd.brenda2_enzyme 
	join brenda2_reaction on brenda2_enzyme.enzyme_id=brenda2_reaction.enzyme_id 
	join brenda2_subtrate2reaction on brenda2_reaction.id = brenda2_subtrate2reaction.reaction_id
	join brenda2_reactand on brenda2_subtrate2reaction.reactand_id = brenda2_reactand.id
	join brenda2_metabolite on brenda2_reactand.metabolite_id = brenda2_metabolite.id
	where ec = '1.1.1.1' and organism_id = 3;

	//get reaction from ec / organism
	SELECT * FROM dawismd.brenda2_enzyme 
	join brenda2_reaction on brenda2_enzyme.enzyme_id=brenda2_reaction.enzyme_id 
	where ec = '1.1.1.1' and organism_id = 3;

	// get substrates of reaction_id
	SELECT * FROM dawismd.brenda2_reaction
	join brenda2_subtrate2reaction on brenda2_reaction.id = brenda2_subtrate2reaction.reaction_id
	join brenda2_reactand on brenda2_subtrate2reaction.reactand_id = brenda2_reactand.id
	join brenda2_metabolite on brenda2_reactand.metabolite_id = brenda2_metabolite.id
	where brenda2_reaction.id = 53;

	//get products of reaction_id
	SELECT * FROM dawismd.brenda2_reaction
	join brenda2_product2reaction on brenda2_reaction.id = brenda2_product2reaction.reaction_id
	join brenda2_reactand on brenda2_product2reaction.reactand_id = brenda2_reactand.id
	join brenda2_metabolite on brenda2_reactand.metabolite_id = brenda2_metabolite.id
	where brenda2_reaction.id = 53;

	//get enzyme by %name%
	SELECT * FROM dawismd.brenda2_synonym
	join brenda2_enzyme on brenda2_synonym.enzyme_id = brenda2_enzyme.enzyme_id
	where brenda2_synonym.name like '%nadh%' or brenda2_enzyme.recommendedName like '%nadh%';

	//get enzyme by %name% and organism_id
	SELECT * FROM dawismd.brenda2_synonym
	join brenda2_enzyme on brenda2_synonym.enzyme_id = brenda2_enzyme.enzyme_id
	where organism_id = 301 and ( brenda2_synonym.name like '%nadh%' or brenda2_enzyme.recommendedName like '%nadh%');

	// get organism by name
	SELECT * FROM dawismd.brenda2_organism
	where brenda2_organism.name like '%hom%';

	// get km by enzyme_id, metabolite_id and organism_id
	SELECT * FROM dawismd.brenda2_km
	where brenda2_km.enzyme_id = 3
	and brenda2_km.metabolite_id = 1
	and brenda2_km.organism_id = 3;

	// get enzyme by metabolite and organism_id
	SELECT * FROM dawismd.brenda2_enzyme 
	join brenda2_reaction on brenda2_enzyme.enzyme_id=brenda2_reaction.enzyme_id
	join brenda2_subtrate2reaction on brenda2_reaction.id = brenda2_subtrate2reaction.reaction_id
	join brenda2_reactand on brenda2_subtrate2reaction.reactand_id = brenda2_reactand.id
	join brenda2_metabolite on brenda2_reactand.metabolite_id = brenda2_metabolite.id
	where brenda2_metabolite.id = 7;

	//get enzyme by metabolite name and organism(substrate)
	select count(*) from (select distinct brenda2_enzyme.* FROM brenda2_enzyme
	join brenda2_reaction on brenda2_enzyme.enzyme_id = brenda2_reaction.enzyme_id
	join brenda2_subtrate2reaction on brenda2_reaction.id = brenda2_subtrate2reaction.reaction_id
	join brenda2_reactand on brenda2_subtrate2reaction.reactand_id = brenda2_reactand.id
	join brenda2_metabolite on brenda2_reactand.metabolite_id = brenda2_metabolite.id
	where brenda2_metabolite.name  = 'h2o' and brenda2_reaction.organism_id = 5) as t;

	//get enzyme by metabolite name and organism(product)
	select count(*) from (select distinct brenda2_enzyme.* FROM brenda2_enzyme
	join brenda2_reaction on brenda2_enzyme.enzyme_id = brenda2_reaction.enzyme_id
	join brenda2_product2reaction on brenda2_reaction.id = brenda2_product2reaction.reaction_id
	join brenda2_reactand on brenda2_product2reaction.reactand_id = brenda2_reactand.id
	join brenda2_metabolite on brenda2_reactand.metabolite_id = brenda2_metabolite.id
	where brenda2_metabolite.name  = 'h2o' and brenda2_reaction.organism_id = 5) as t;

	// get organims by set of IDs
	select * from brenda2_organism
	where brenda2_organism.id in (1,2);



	select count(*) from (select distinct brenda2_enzyme.* FROM brenda2_enzyme
	join brenda2_reaction on brenda2_enzyme.enzyme_id = brenda2_reaction.enzyme_id
	join brenda2_product2reaction on brenda2_reaction.id = brenda2_product2reaction.reaction_id
	join brenda2_subtrate2reaction on brenda2_reaction.id = brenda2_subtrate2reaction.reaction_id
	join brenda2_reactand as prod on brenda2_product2reaction.reactand_id = prod.id
	join brenda2_reactand as sub on brenda2_subtrate2reaction.reactand_id = sub.id
	join brenda2_metabolite as subM on sub.metabolite_id = subM.id
	join brenda2_metabolite as prodM on prod.metabolite_id = prodM.id
	where prodM.name = 'h2o' or subM.name  = 'h2o') as t;

*/
}
