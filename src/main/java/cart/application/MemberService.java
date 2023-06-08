package cart.application;

import cart.domain.Member;
import cart.domain.Point;
import cart.dto.request.MemberCreateRequest;
import cart.dto.response.MemberCreateResponse;
import cart.dto.response.MemberPointQueryResponse;
import cart.dto.response.MemberQueryResponse;
import cart.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberCreateResponse join(final MemberCreateRequest request) {
        final Member member = memberRepository.addMember(new Member(request.getEmail(), request.getPassword()), Point.joinEvent().getPoint());
        return MemberCreateResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberPointQueryResponse findPointsOf(final Member member) {
        return new MemberPointQueryResponse(memberRepository.findPointOf(member));
    }

    @Transactional
    public void addPoints(final Member member, final int points) {
        memberRepository.addPoint(member, points);
    }

    @Transactional(readOnly = true)
    public List<MemberQueryResponse> findAllMembers() {
        final List<Member> members = memberRepository.findAllMembers();
        return members.stream()
                .map(MemberQueryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberQueryResponse findMemberById(final Long id) {
        return MemberQueryResponse.from(memberRepository.findMemberById(id));
    }
}
