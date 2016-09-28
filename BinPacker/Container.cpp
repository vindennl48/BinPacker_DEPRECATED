#include "Container.h"
#include<sstream>
#include<iomanip>
using namespace std;

vector<Container> Container::winners;
vector<Table> Container::sTables_all;
int Container::sAllowedMissing;
double Container::offsetX;
double Container::offsetY;
mutex Container::_mutex;


//vector<double> Container::maxPriceList;
Container::Container() {}


Container::Container(vector<Table> all_tables) :
	Rect(){
	tables_all = all_tables;
	tables_left = all_tables;
	//for (int i = 0; i < all_tables.size(); i++) {
	//	tables_all.push_back(all_tables[i]);
	//	tables_left.push_back(all_tables[i]);
	//}
	allowedMissing = sAllowedMissing;
}


Container::~Container(){}


bool Container::addFirstTable(Table *table){

	Rect rect;
	rect.width = width;
	rect.length = length;
	rect.origin = origin;

	if (table->isInsideOf(rect)) {
		tables_used.push_back(*table);
		removeTable(table);
		addPoints(table);
		return true;
	}
	return false;
}


bool Container::removeTable(Table *table){
	bool answer = false;
	for (int i = 0; i < tables_left.size(); i++) {
		Table *iTable = &tables_left[i];
		if (table->name == iTable->name) {
			tables_left.erase(tables_left.begin() + i);
			i = -1;
			answer = true;
		}
	}
	return answer;
}


bool Container::addPoints(Table *table){
	Point point1,point2;

	point1.x = table->getLeft();
	point1.y = table->getTop();
	point2.x = table->getRight();
	point2.y = table->getBottom();

	bool point1tf = false;
	bool point2tf = false;
	for (int i = 0; i < points.size(); i++) {
		if (point1.isEqualTo(points[i])) {
			point1tf = true;
		}
		if (point2.isEqualTo(points[i])) {
			point2tf = true;
		}
		if (table->origin.isEqualTo(points[i])) {
			points.erase(points.begin() + i);
			i = -1;
		}
	}

	if (!point1tf) {
		points.push_back(point1);
	}
	if (!point2tf) {
		points.push_back(point2);
	}

	return true;
}


bool Container::runTables(){
//	cout << "running tables" << endl;

	int tblsLft = tables_left.size();

/*	if (tblsLft == 0) {
		cout << "Result Found With All Tables!" << endl;
		printLayout();
		pause;
		return true;
	}
	else */if (tblsLft <= allowedMissing) {
		//cout << "Found Layout with " << (tblsLft/2) <<
		//	" table(s) missing." << endl;
		//cout << "Sales Value: " << getTotal() << endl;

		///*---------------------------*/
		//cout << "width: " << width << ", Length: " << length << endl;
		///*---------------------------*/

		_mutex.lock();
		addToWinners(getInst());
		_mutex.unlock();
		////printLayout();
		//cout << "To continue for a better result, press 'Enter'..." << endl;
		//pause;
	}

	for (int i = 0; i < tables_left.size(); i++) {
		Table table = tables_left[i];

		for (int j = 0; j < points.size(); j++) {
			Point* point = &points[j];

			table.origin = *point;

			if (doesFit(&table)) {
//				sp "does fit tbls_left: " ee tblsLft ee ", allowed missing: " ee allowedMissing eak;
//				pause;
				Container c = getInst();
				c.tables_used.push_back(table);
				c.removeTable(&table);
				c.addPoints(&table);
				if (c.runTables()) {
					return true;
				}
			}
		}
	}

//	sp "returned false" eak;
//	pause;
	return false;
}


bool Container::doesFit(Table *table){
	Rect rect;
	rect.width = width;
	rect.length = length;
	rect.origin = origin;

	if (!table->isInsideOf(rect)) {
//		cout << "not inside" << endl;
		return false;
	}
//	else {
//		cout << "is inside" << endl;
//	}

	for (int i = 0; i < tables_used.size(); i++) {
		Table* iTable = &tables_used[i];

		if (table->overlaps(*iTable)) {
			return false;
		}
		else if (table->isInsideOf(*iTable)) {
			return false;
		}
	}

	return true;
}


double Container::getTotal(){
	double answer = 0;

	for (int i = 0; i < tables_used.size(); i++) {
		answer += tables_used[i].price;
	}

	return answer;
}


#define PRECISION  4
string Container::printLayout() {
	stringstream buffer;

	buffer << "rectangle \r\n";
	buffer << fixed << setprecision(PRECISION) << (0 + offsetX) << ",";
	buffer << fixed << setprecision(PRECISION) << (0 + offsetY) << "\r\n";
	buffer << fixed << setprecision(PRECISION) << (width + offsetX) << ",";
	buffer << fixed << setprecision(PRECISION) << (length + offsetY) << "\r\n\r\n";

	for (int i = 0; i < tables_used.size(); i++) {
		Table* table = &tables_used[i];

		buffer << fixed << setprecision(PRECISION) << (table->getLeft() + offsetX) << ",";
		buffer << fixed << setprecision(PRECISION) << (table->getBottom() + offsetY) << "\r\n";
		buffer << fixed << setprecision(PRECISION) << (table->getRight() + offsetX) << ",";
		buffer << fixed << setprecision(PRECISION) << (table->getTop() + offsetY) << "\r\n\r\n";
	}

	offsetX += 53.5;
	return buffer.str();
	//toClipboard(buffer.str());
	//cout << "Result Copied To ClipBoard" << endl;
}


void Container::toClipboard(const std::string &s) {
	OpenClipboard(0);
	EmptyClipboard();
	HGLOBAL hg = GlobalAlloc(GMEM_MOVEABLE, s.size());
	if (!hg) {
		CloseClipboard();
		return;
	}
	memcpy(GlobalLock(hg), s.c_str(), s.size());
	GlobalUnlock(hg);
	SetClipboardData(CF_TEXT, hg);
	CloseClipboard();
	GlobalFree(hg);
}


Container Container::getInst(){

	Container c(tables_all);

	c.length = length;
	c.width = width;
	c.origin = origin;
	c.points = points;
	c.tables_left = tables_left;
	c.tables_used = tables_used;

	return c;
}


bool Container::addToWinners(Container c){
	double this_total = c.getTotal();

	if (!winners.empty()) {
		double winners_total = winners[0].getTotal();
		if (this_total == winners_total) {
			winners.push_back(c);
			return true;
		}
		else if (this_total > winners_total) {
			winners.clear();
			winners.push_back(c);
			return true;
		}
	}
	else {
		winners.push_back(c);
	}

	return false;
}
