INSERT INTO priority_types VALUES (default, 'Low', 1 );
INSERT INTO priority_types VALUES (default, 'Medium', 2 );
INSERT INTO priority_types VALUES (default, 'High', 3 );

INSERT INTO specifications VALUES (default, 'Accounts Receivable', 'Describe system', 'Notes on the new ERP system', 1);
INSERT INTO specifications VALUES (default, 'Accounts Payable', 'Describe system', 'Notes on the new ERP system', 1);

INSERT INTO stakeholders VALUES (default, 1, 'Accounts Receivable Clerk', 'Describe Accounts Receivable', 'Notes on Accounts Receivable stakeholder', 1);
INSERT INTO stakeholders VALUES (default, 1, 'Manager', 'Describe Manager', 'Notes on Manager', 1);
INSERT INTO stakeholders VALUES (default, 1, 'Customer','Describe Customer', 'Notes on Customer', 1);

INSERT INTO stakeholders VALUES (default, 2, 'Accounts Payable Clerk', 'Describe Accounts Payable', 'Notes on Accounts Payable stakeholder', 1);
INSERT INTO stakeholders VALUES (default, 2, 'Supplier', 'Describe Supplier', 'Notes on Supplier stakeholder', 1);

INSERT INTO specification_objectives VALUES (default, 1, 'Objective 1-1', 'Increase productivity by 25%',  'Additional notes', 3);
INSERT INTO specification_objectives VALUES (default, 1, 'Objective 1-2',  'Reduce errors by 70%', 'Additional notes', 2);
INSERT INTO specification_objectives VALUES (default, 1, 'Objective 2-1', 'See outstanding receivables accurate to within 24 hours', 'Additional notes', 1);
INSERT INTO specification_objectives VALUES (default, 1, 'Objective 2-2', 'Suspend customer activity when outstanding balance > $6,000',  'Additional notes', 3);
INSERT INTO specification_objectives VALUES (default, 1, 'Objective 3-1', 'Have ability to see status of orders online', 'Additional notes', 1);
INSERT INTO specification_objectives VALUES (default, 1, 'Objective 3-2', 'Update address online', 'Additional notes', 1);

INSERT INTO specification_objectives VALUES (default, 2, 'Objective 4-1', 'Take all supplier discounts',  'Additional notes', 2);
INSERT INTO specification_objectives VALUES (default, 2, 'Objective 4-2', 'Reduce errors by 70%', 'Additional notes', 3);
INSERT INTO specification_objectives VALUES (default, 2, 'Objective 4-3', 'See status of invoices', 'Additional notes', 1);
INSERT INTO specification_objectives VALUES (default, 2, 'Objective 4-4', 'Update address online', 'Additional notes', 1);

INSERT INTO stakeholder_objectives VALUES (default, 1, 1, 'Additional notes', 3);
INSERT INTO stakeholder_objectives VALUES (default, 1, 2, 'Additional notes', 2);

INSERT INTO stakeholder_objectives VALUES (default, 2, 3, 'Additional notes', 1);
INSERT INTO stakeholder_objectives VALUES (default, 2, 4, 'Additional notes', 3);

INSERT INTO stakeholder_objectives VALUES (default, 3, 5, 'Additional notes', 1);
INSERT INTO stakeholder_objectives VALUES (default, 3, 6, 'Additional notes', 1);

INSERT INTO stakeholder_objectives VALUES (default, 4, 7, 'Additional notes', 2);
INSERT INTO stakeholder_objectives VALUES (default, 4, 8, 'Additional notes', 3);
INSERT INTO stakeholder_objectives VALUES (default, 4, 9, 'Additional notes', 1);
INSERT INTO stakeholder_objectives VALUES (default, 4, 10, 'Additional notes', 1);


INSERT INTO specification_templates VALUES ( default, 'Major Product', 'Describe product here', 'Notes on major product', 1);
INSERT INTO specification_templates VALUES ( default, 'Minor Product', 'Describe product here', 'Notes on minor product', 1);