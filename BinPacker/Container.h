#pragma once
#include"NCLDS.h"
#include"Rect.h"
#include"Table.h"


class Container :
	public Rect
{
public:
	Container();
	~Container();
	static std::vector<Container> winners;
	static std::vector<Table> tables_all;
//	static std::vector<double> maxPriceList;
	static int allowedMissing;
	std::vector<Table*> tables_left;
	std::vector<Table> tables_used;
	std::vector<Point> points;
	static double offsetX;
	static double offsetY;
	bool addFirstTable(Table *table);
	bool removeTable(Table *table);
	bool addPoints(Table *table);
	bool runTables();
	bool doesFit(Table *table);
	double getTotal();
	void printLayout();
	static void toClipboard(const std::string &s);
	Container getInst();
	static bool addToWinners(Container c);
};
