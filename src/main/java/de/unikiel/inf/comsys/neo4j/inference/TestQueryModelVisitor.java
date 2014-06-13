
package de.unikiel.inf.comsys.neo4j.inference;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.Add;
import org.openrdf.query.algebra.And;
import org.openrdf.query.algebra.ArbitraryLengthPath;
import org.openrdf.query.algebra.Avg;
import org.openrdf.query.algebra.BNodeGenerator;
import org.openrdf.query.algebra.BindingSetAssignment;
import org.openrdf.query.algebra.Bound;
import org.openrdf.query.algebra.Clear;
import org.openrdf.query.algebra.Coalesce;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.CompareAll;
import org.openrdf.query.algebra.CompareAny;
import org.openrdf.query.algebra.Copy;
import org.openrdf.query.algebra.Count;
import org.openrdf.query.algebra.Create;
import org.openrdf.query.algebra.Datatype;
import org.openrdf.query.algebra.DeleteData;
import org.openrdf.query.algebra.DescribeOperator;
import org.openrdf.query.algebra.Difference;
import org.openrdf.query.algebra.Distinct;
import org.openrdf.query.algebra.EmptySet;
import org.openrdf.query.algebra.Exists;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ExtensionElem;
import org.openrdf.query.algebra.Filter;
import org.openrdf.query.algebra.FunctionCall;
import org.openrdf.query.algebra.Group;
import org.openrdf.query.algebra.GroupConcat;
import org.openrdf.query.algebra.GroupElem;
import org.openrdf.query.algebra.IRIFunction;
import org.openrdf.query.algebra.If;
import org.openrdf.query.algebra.In;
import org.openrdf.query.algebra.InsertData;
import org.openrdf.query.algebra.Intersection;
import org.openrdf.query.algebra.IsBNode;
import org.openrdf.query.algebra.IsLiteral;
import org.openrdf.query.algebra.IsNumeric;
import org.openrdf.query.algebra.IsResource;
import org.openrdf.query.algebra.IsURI;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.Label;
import org.openrdf.query.algebra.Lang;
import org.openrdf.query.algebra.LangMatches;
import org.openrdf.query.algebra.LeftJoin;
import org.openrdf.query.algebra.Like;
import org.openrdf.query.algebra.ListMemberOperator;
import org.openrdf.query.algebra.Load;
import org.openrdf.query.algebra.LocalName;
import org.openrdf.query.algebra.MathExpr;
import org.openrdf.query.algebra.Max;
import org.openrdf.query.algebra.Min;
import org.openrdf.query.algebra.Modify;
import org.openrdf.query.algebra.Move;
import org.openrdf.query.algebra.MultiProjection;
import org.openrdf.query.algebra.Namespace;
import org.openrdf.query.algebra.Not;
import org.openrdf.query.algebra.Or;
import org.openrdf.query.algebra.Order;
import org.openrdf.query.algebra.OrderElem;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.ProjectionElemList;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.QueryModelVisitor;
import org.openrdf.query.algebra.QueryRoot;
import org.openrdf.query.algebra.Reduced;
import org.openrdf.query.algebra.Regex;
import org.openrdf.query.algebra.SameTerm;
import org.openrdf.query.algebra.Sample;
import org.openrdf.query.algebra.Service;
import org.openrdf.query.algebra.SingletonSet;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Str;
import org.openrdf.query.algebra.Sum;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.ZeroLengthPath;

public class TestQueryModelVisitor implements QueryModelVisitor {

	@Override
	public void meet(QueryRoot qr) throws Exception {
		
	}

	@Override
	public void meet(Add add) throws Exception {
		
	}

	@Override
	public void meet(And and) throws Exception {
		
	}

	@Override
	public void meet(ArbitraryLengthPath alp) throws Exception {
		
	}

	@Override
	public void meet(Avg avg) throws Exception {
		
	}

	@Override
	public void meet(BindingSetAssignment bsa) throws Exception {
		
	}

	@Override
	public void meet(BNodeGenerator bng) throws Exception {
		
	}

	@Override
	public void meet(Bound bound) throws Exception {
		
	}

	@Override
	public void meet(Clear clear) throws Exception {
		
	}

	@Override
	public void meet(Coalesce clsc) throws Exception {
		
	}

	@Override
	public void meet(Compare cmpr) throws Exception {
		
	}

	@Override
	public void meet(CompareAll ca) throws Exception {
		
	}

	@Override
	public void meet(CompareAny ca) throws Exception {
		
	}

	@Override
	public void meet(DescribeOperator d) throws Exception {
		
	}

	@Override
	public void meet(Copy copy) throws Exception {
		
	}

	@Override
	public void meet(Count count) throws Exception {
		
	}

	@Override
	public void meet(Create create) throws Exception {
		
	}

	@Override
	public void meet(Datatype dtp) throws Exception {
		
	}

	@Override
	public void meet(DeleteData dd) throws Exception {
		
	}

	@Override
	public void meet(Difference dfrnc) throws Exception {
		
	}

	@Override
	public void meet(Distinct dstnct) throws Exception {
		
	}

	@Override
	public void meet(EmptySet es) throws Exception {
		
	}

	@Override
	public void meet(Exists exists) throws Exception {
		
	}

	@Override
	public void meet(Extension extnsn) throws Exception {
		
	}

	@Override
	public void meet(ExtensionElem ee) throws Exception {
		
	}

	@Override
	public void meet(Filter filter) throws Exception {
		
	}

	@Override
	public void meet(FunctionCall fc) throws Exception {
		
	}

	@Override
	public void meet(Group group) throws Exception {
		
	}

	@Override
	public void meet(GroupConcat gc) throws Exception {
		
	}

	@Override
	public void meet(GroupElem ge) throws Exception {
		
	}

	@Override
	public void meet(If i) throws Exception {
		
	}

	@Override
	public void meet(In in) throws Exception {
		
	}

	@Override
	public void meet(InsertData id) throws Exception {
		
	}

	@Override
	public void meet(Intersection i) throws Exception {
		
	}

	@Override
	public void meet(IRIFunction irif) throws Exception {
		
	}

	@Override
	public void meet(IsBNode ibn) throws Exception {
		
	}

	@Override
	public void meet(IsLiteral il) throws Exception {
		
	}

	@Override
	public void meet(IsNumeric in) throws Exception {
		
	}

	@Override
	public void meet(IsResource ir) throws Exception {
		
	}

	@Override
	public void meet(IsURI isuri) throws Exception {
		
	}

	@Override
	public void meet(Join join) throws Exception {
		
	}

	@Override
	public void meet(Label label) throws Exception {
		
	}

	@Override
	public void meet(Lang lang) throws Exception {
		
	}

	@Override
	public void meet(LangMatches lm) throws Exception {
		
	}

	@Override
	public void meet(LeftJoin lj) throws Exception {
		
	}

	@Override
	public void meet(Like like) throws Exception {
		
	}

	@Override
	public void meet(Load load) throws Exception {
		
	}

	@Override
	public void meet(LocalName ln) throws Exception {
		
	}

	@Override
	public void meet(MathExpr me) throws Exception {
		
	}

	@Override
	public void meet(Max max) throws Exception {
		
	}

	@Override
	public void meet(Min min) throws Exception {
		
	}

	@Override
	public void meet(Modify modify) throws Exception {
		
	}

	@Override
	public void meet(Move move) throws Exception {
		
	}

	@Override
	public void meet(MultiProjection mp) throws Exception {
		
	}

	@Override
	public void meet(Namespace nmspc) throws Exception {
		
	}

	@Override
	public void meet(Not not) throws Exception {
		
	}

	@Override
	public void meet(Or or) throws Exception {
		
	}

	@Override
	public void meet(Order order) throws Exception {
		
	}

	@Override
	public void meet(OrderElem oe) throws Exception {
		
	}

	@Override
	public void meet(Projection prjctn) throws Exception {
		
	}

	@Override
	public void meet(ProjectionElem pe) throws Exception {
		
	}

	@Override
	public void meet(ProjectionElemList pel) throws Exception {
		
	}

	@Override
	public void meet(Reduced rdcd) throws Exception {
		
	}

	@Override
	public void meet(Regex regex) throws Exception {
		
	}

	@Override
	public void meet(SameTerm st) throws Exception {
		
	}

	@Override
	public void meet(Sample sample) throws Exception {
		
	}

	@Override
	public void meet(Service srvc) throws Exception {
		
	}

	@Override
	public void meet(SingletonSet ss) throws Exception {
		
	}

	@Override
	public void meet(Slice slice) throws Exception {
		
	}

	@Override
	public void meet(StatementPattern sp) throws Exception {
		Var p = sp.getPredicateVar();
		if (p.isConstant()) {
			Value val = p.getValue();
			if (val instanceof URI) {
				URI uri = (URI) val;
				
				sp.replaceWith(new ArbitraryLengthPath(
					sp.getSubjectVar(),
					new EmptySet(),
					sp.getObjectVar(),
					1)
				);
			}
		}
	}

	@Override
	public void meet(Str str) throws Exception {
		
	}

	@Override
	public void meet(Sum sum) throws Exception {
		
	}

	@Override
	public void meet(Union union) throws Exception {
		
	}

	@Override
	public void meet(ValueConstant vc) throws Exception {
		
	}

	@Override
	public void meet(ListMemberOperator lmo) throws Exception {
		
	}

	@Override
	public void meet(Var var) throws Exception {
		
	}

	@Override
	public void meet(ZeroLengthPath zlp) throws Exception {
		
	}

	@Override
	public void meetOther(QueryModelNode qmn) throws Exception {
		
	}
	
}
