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


-- INSERT INTO objectives VALUES (default, 1, 'Increase productivity by 25%',  '', '', 3 );
-- INSERT INTO objectives VALUES (default, 1, 'Reduce errors by 70%,', '', '', 2 );

-- INSERT INTO objectives VALUES (default, 2, 'Objective 1', 'See outstanding receivables accurate to within 24 hours', '', 1 );
-- INSERT INTO objectives VALUES (default, 2, 'Objective 2', 'Suspend customer activity when outstanding balance > $6,000',  '', 3 );

-- INSERT INTO objectives VALUES (default, 3, 'Objective 1', 'Have ability to see status of orders online', '', 1 );
-- INSERT INTO objectives VALUES (default, 3, 'Objective 2', 'Update address online', '', 1 );

-- INSERT INTO objectives VALUES (default, 4, 'Objective 1', 'Take all supplier discounts',  '', 2 );
-- INSERT INTO objectives VALUES (default, 4, 'Objective 2', 'Reduce errors by 70%', '', 3 );

-- INSERT INTO objectives VALUES (default, 4, 'Objective 1', 'See status of invoices', '', 1 );
-- INSERT INTO objectives VALUES (default, 4, 'Objective 2', 'Update address online', '', 1 );


INSERT INTO specification_templates VALUES ( default, 'Major Product', 'Describe product here', 'Notes on major product', 1);
INSERT INTO specification_templates VALUES ( default, 'Minor Product', 'Describe product here', 'Notes on minor product', 1);